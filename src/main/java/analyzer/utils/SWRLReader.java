package analyzer.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLResultValue;
import uk.ac.manchester.cs.jfact.JFactFactory;
import java.io.File;
import java.util.*;

/**
 * Klasa służy do przetwarzania plików RDF w celu analizy i grupowania reguł SWRL.
 * @author Adrian Kaczmarek Krzysztof Rózga Radosław Kapłon
 * @version 1.0
 */
public class SWRLReader
{
    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private SWRLRuleEngine ruleEngine;
    private OWLReasonerFactory reasonerFactory;
    private OWLReasoner reasoner;
    private SearchRulesSpy spy;

    /**
     * Konstruktor klasy SWRLReader przyjmuje jako argument ścieżkę do pliku RDF.
     * @param filePath
     */
    public SWRLReader(String filePath)
    {
        try
        {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
            reasonerFactory = new JFactFactory();
            OWLReasonerConfiguration config = new SimpleConfiguration(50000);
            reasoner = this.reasonerFactory.createReasoner(ontology, config);
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            spy = new SearchRulesSpy();
        }
        catch (OWLOntologyCreationException e)
        {
            e.printStackTrace();
        }
    }

    public SearchRulesSpy getSpy()
    {
        return spy;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getRules() {
        Map<String, String> rules = new HashMap<>();

        Set<SWRLAPIRule> rulesSet = ruleEngine.getSWRLRules();

        for (SWRLAPIRule rule : rulesSet) {
            String rulename = "";
            String ruleformula = "";

            for (SWRLAtom atom : rule.getBody()) {
                if (checkForValues(atom.getPredicate())) {
                    ruleformula += getValueFromPredicate(atom.getPredicate()) + "(";
                    String swrlArguments = "";
                    for (SWRLArgument argument : atom.getAllArguments()) {
                        swrlArguments += getValueFromArgument(argument);
                    }
                    swrlArguments = insertCommasIntoString(swrlArguments);
                    ruleformula += swrlArguments + ")";
                }
                ruleformula += " ^ ";
            }

            ruleformula = ruleformula.substring(0, ruleformula.length()-2);

            ruleformula += " => ";

            for (SWRLAtom atom : rule.getHead()) {
                if (checkForValues(atom.getPredicate())) {
                    rulename = getValueFromPredicate(atom.getPredicate());
                    ruleformula += rulename + "(";
                    String swrlArguments = "";
                    for (SWRLArgument argument : atom.getAllArguments()) {
                        swrlArguments += getValueFromArgument(argument);
                    }
                    swrlArguments = insertCommasIntoString(swrlArguments);
                    ruleformula += swrlArguments + ")";
                }
            }
            rules.put(rulename, ruleformula);
        }

        return rules;
    }

    /**
     * Funkcja zwraca klasy ontologii w postaci listy typu String.
     * @return
     */
    public List<String> getClasses() {
        List<String> classes = new ArrayList<>();
        Set<OWLClass> owlClasses = ontology.getClassesInSignature();

        for (OWLClass owlClass : owlClasses)
            if(checkForValues(owlClass)) {
                classes.add(getValueSubstring(owlClass.toString()));
            }

        return classes;
    }

    /**
     *
     * @param className
     * @return
     */
    public List<String> getRulesNamesForClass(String className)
    {
        OWLClass filteringClass = null;
        Set<OWLClass> owlClasses = ontology.getClassesInSignature();
        List<String> ruleNames = new ArrayList<>();
        Map<String, List<String>> next_level_map = new HashMap<>();
        Map<String, List<String>> current_level_map = new HashMap<>();

        for (OWLClass owlClass : owlClasses)
        {
            if(owlClass.getIRI().getShortForm().equals(className))
            {
                filteringClass = owlClass;
                break;
            }
        }

        if(filteringClass != null)
        {
            NodeSet<OWLClass> superClassesInNode = reasoner.getSuperClasses(filteringClass, true);
            Set<OWLClass> classesToAnalyze = new HashSet<>();

            for (OWLClass superClass : superClassesInNode.getFlattened())
            {
                if(isGoodClass(superClass))
                {
                    classesToAnalyze.add(superClass);
                }
            }

            classesToAnalyze.add(filteringClass);

            for(OWLClass owlClass : classesToAnalyze)
            {
                Set<OWLClassAxiom> axioms = ontology.getAxioms(owlClass);
                List<String> class_rule_names = new ArrayList<>();

                for (OWLClassAxiom axiom : axioms)
                {
                    Set<OWLObjectProperty> props = axiom.getObjectPropertiesInSignature();

                    for (OWLObjectProperty prop : props)
                    {
                        ruleNames.add(getValueSubstring(prop.toString()));
                        class_rule_names.add(getValueSubstring(prop.toString()));
                    }
                }

                if(owlClass == filteringClass)
                {
                    current_level_map.put(owlClass.getIRI().getShortForm(), class_rule_names);
                }
                else
                {
                    next_level_map.put(owlClass.getIRI().getShortForm(), class_rule_names);
                }
            }
        }

        spy.setClassRuleMap(current_level_map);
        spy.setNextLevel(next_level_map);

        return ruleNames;
    }

    /**
     * Funkcja za pomocą zapytań SQWRL zwraca listę funkcyjnych obiektów.
     * @return
     */
    public List<String> getFunctionalObjectProperty()
    {
        String query = "tbox:fopa(?v) -> sqwrl:select(?v)";
        return execute(query);
    }

    /**
     * Funkcja za pomocą zapytań SQWRL zwraca listę symetrycznych obiektów.
     * @return
     */
    public List<String> getSymmetricObjectProperty()
    {
        String query = "rbox:spa(?v) -> sqwrl:select(?v)";
        return execute(query);
    }

    /**
     * Funkcja za pomocą zapytań SQWRL zwraca listę SubObiektów.
     * @param rule
     * @return
     */
    public List<String> getSubOfObjectProperty(String rule)
    {
        String query = "rbox:sopa(" + rule + ", ?v) -> sqwrl:select(?v)";
        return execute(query);
    }

    /**
     * Funkcja za pomocą zapytań SQWRL zwraca listę odwrotnych obiektów.
     * @param rule
     * @return
     */
    public List<String> getInverseObjectProperty(String rule)
    {
        String query = "rbox:iopa(?v, " + rule + ") -> sqwrl:select(?v)";
        return execute(query);
    }

    public List<String> getInverseProperty(String rule)
    {
        String query = "rbox:iopa(" + rule + ", ?v) -> sqwrl:select(?v)";
        return execute(query);
    }


    /**
     *
     * @param owlClass
     * @return
     */
    private boolean isGoodClass(OWLClass owlClass)
    {
        boolean toReturn = true;
        String tmp = owlClass.getIRI().toString();

        if(tmp.contains("Thing") || tmp.contains("Nothing"))
        {
            toReturn = false;
        }

        return toReturn;
    }

    /**
     * Funkcja sprawdza czy w predykacie zawarte są wartości.
     * @param predicate
     * @return
     */
    private boolean checkForValues(SWRLPredicate predicate) {
        String s = predicate.toString();
        String v1 = "#";
        if (s.contains(v1))
            return true;
        else
            return false;
    }

    /**
     * Funkcja sprawdza czy w klasie OWL zawarte są wartości.
     * @param owlClass
     * @return
     */
    private boolean checkForValues(OWLClass owlClass) {
        String s = owlClass.toString();
        String v1 = "#";
        if (s.contains(v1))
            return true;
        else
            return false;
    }

    /**
     *  Funkcja wyciąga z predykatu wartość.
     * @param predicate
     * @return
     */
    private String getValueFromPredicate(SWRLPredicate predicate) {
        String tmp = predicate.toString();
        return getValueSubstring(tmp);
    }

    /**
     *  Funkcja wyciąga z SWRLArgument wartość.
     * @param argument
     * @return
     */
    private String getValueFromArgument(SWRLArgument argument) {
        String tmp = argument.toString();
        return getValueSubstring(tmp);
    }

    /**
     * Funkcja pomocnicza wyciągająca wartość.
     * @param s
     * @return
     */
    private String getValueSubstring(String s)
    {
        return s.substring(s.indexOf("#") + 1, s.indexOf(">"));
    }

    /**
     * Funkcja pomocnicza zamieniająca w ciągu znaków kropki na przecinki.
     * @param s
     * @return
     */
    private String insertCommasIntoString(String s) {
        s = s.replaceAll(".", "$0, ").trim();
        s = s.substring(0, s.length()-1);

        return s;
    }

    /**
     * Funkcja służy do wykonywania zapytań na silniku reguł SQWRL (SQWRL Engine) i zwracania wyników tych zapytań.
     * @param query
     * @return
     */
    private List<String> execute(String query) {
        List<String> rules = new ArrayList<>();

        try {
            SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
            SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);

            for (SQWRLResultValue rule : result.getColumn(0)) {
                rules.add(rule.toString().substring(1));
            }

        } catch (SWRLParseException e) {
            e.printStackTrace();
        } catch (SQWRLException e) {
            e.printStackTrace();
        }

        return rules;
    }
}
