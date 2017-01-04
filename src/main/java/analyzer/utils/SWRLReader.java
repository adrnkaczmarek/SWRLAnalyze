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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<String> getRules() {
        List<String> rules = new ArrayList<String>();

        Set<SWRLAPIRule> set = ruleEngine.getSWRLRules();

        for (SWRLAPIRule rule : set) {
            String swrlRule = "";


            for (SWRLAtom atom : rule.getBody()) {
                System.out.println(atom.getPredicate());
                if (checkForValues(atom.getPredicate().toString())) {
                    //System.out.println(getValueFromPredicate(atom.getPredicate()));
                    swrlRule += getValueFromPredicate(atom.getPredicate()) + "(";
                    String swrlArguments = "";
                    for (SWRLArgument argument : atom.getAllArguments()) {
                        //System.out.println(getValueFromArgument(argument));
                        //swrlRule += getValueFromArgument(argument) + " ";
                        swrlArguments += getValueFromArgument(argument);
                    }
                    swrlArguments = insertCommasIntoString(swrlArguments);
                    swrlRule += swrlArguments + ")";
                }

                swrlRule += " ^ ";
            }

            swrlRule = swrlRule.substring(0, swrlRule.length()-2);

            swrlRule += " => ";

            String headRule = "";
            for (SWRLAtom atom : rule.getHead()) {
                //System.out.println(atom.getPredicate());
                if (checkForValues(atom.getPredicate().toString())) {
                    //System.out.println(getValueFromPredicate(atom.getPredicate()));
                    swrlRule += getValueFromPredicate(atom.getPredicate()) + "(";
                    String swrlArguments = "";
                    for (SWRLArgument argument : atom.getAllArguments()) {
                        //System.out.println(getValueFromArgument(argument));
                        //swrlRule += getValueFromArgument(argument) + " ";
                        swrlArguments += getValueFromArgument(argument);
                    }
                    swrlArguments = insertCommasIntoString(swrlArguments);
                    swrlRule += swrlArguments + ")";
                }
            }
            rules.add(swrlRule);
            //System.out.println(swrlRule);
            //System.out.println("--------");
        }
        return rules;
    }

    public List<String> getRulesNamesForClass(String className) {
        String query = "tbox:opra(?v, " + className + ") -> sqwrl:select(?v)";
        List<String> rules = new ArrayList<String>();

        try {
            SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
            SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);

            for (SQWRLResultValue rule : result.getColumn(0)) {
                rules.add(rule.toString());
            }

        } catch (SWRLParseException e) {
            e.printStackTrace();
        } catch (SQWRLException e) {
            e.printStackTrace();
        }

        return rules;
    }

    private boolean checkForValues(String s) {
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

    /*public void printRules() {
        Set<SWRLAPIRule> set = ruleEngine.getSWRLRules();

        for (SWRLAPIRule elem : set) {

            for (SWRLAtom atom : elem.getHead()) {

                System.out.println(atom.getPredicate() + "  ");
                System.out.println(atom.getAllArguments() + " \n");


            }

            for (SWRLAtom atom : elem.getBody()) {
                System.out.println(atom.getPredicate() + "  ");
                System.out.println(atom.getAllArguments() + " \n");
            }
            System.out.println("--------");
        }
    }

*/
    public List<String> getClasses()
    {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        List<String> classes_list = new ArrayList<>();

        for(OWLClass owl_class : classes)
        {
            classes_list.add(owl_class.toString());
        }

        return classes_list;
    }
}
