package analyzer.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLResultValue;

import java.io.File;
import java.util.*;

public class SWRLReader {
    OWLOntology ontology;
    OWLOntologyManager  manager;
    SWRLRuleEngine ruleEngine;

    public SWRLReader(String filePath) {
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getRules() {
        Map<String, String> rules = new HashMap<String, String>();

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

            String headRule = "";
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

    public List<String> getClasses() {
        List<String> classes = new ArrayList<String>();
        Set<OWLClass> owlClasses = ontology.getClassesInSignature();

        for (OWLClass owlClass : owlClasses)
            if(checkForValues(owlClass)) {
                classes.add(getValueSubstring(owlClass.toString()));
            }

        return classes;
    }

    public List<String> getRulesNamesForClass(String className) {
        String query = "tbox:opra(?v, " + className + ") -> sqwrl:select(?v)";
        List<String> rules = new ArrayList<String>();

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

    public List<String> getFunctionalObjectProperty() {
        String query = "tbox:fopa(?v) -> sqwrl:select(?v)";
        return execute(query);
    }

    public List<String> getSymmetricObjectProperty() {
        String query = "rbox:spa(?v) -> sqwrl:select(?v)";
        return execute(query);
    }

    private boolean checkForValues(SWRLPredicate predicate) {
        String s = predicate.toString();
        String v1 = "#";
        if (s.contains(v1))
            return true;
        else
            return false;
    }

    private boolean checkForValues(OWLClass owlClass) {
        String s = owlClass.toString();
        String v1 = "#";
        if (s.contains(v1))
            return true;
        else
            return false;
    }

    private String getValueFromPredicate(SWRLPredicate predicate) {
        String tmp = predicate.toString();
        return getValueSubstring(tmp);
    }

    private String getValueFromArgument(SWRLArgument argument) {
        String tmp = argument.toString();
        return getValueSubstring(tmp);
    }

    private String getValueSubstring(String s) {
        return s.substring(s.indexOf("#") + 1, s.indexOf(">"));
    }

    private String insertCommasIntoString(String s) {
        s = s.replaceAll(".", "$0, ").trim();
        s = s.substring(0, s.length()-1);

        return s;
    }

    private List<String> execute(String query) {
        List<String> rules = new ArrayList<String>();

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
