package analyzer.controllers;

import analyzer.utils.SWRLReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ViewController
{
    //private SWRLReader reader;
    private Map<String, String> rulesMap;

    @RequestMapping("/index")
    public String index(Model model)
    {
        SWRLReader reader = new SWRLReader(
                "/Users/krzysztof/IdeaProjects/SWRLAnalyzer/src/main/resources/family.xml");
        rulesMap = reader.getRules();
        String[] rules = rulesMap.values().toArray(new String[0]);
        List<String> classes_list = reader.getClasses();
        classes_list.add(0, "None");
        String[] classes = classes_list.toArray(new String[0]);
        model.addAttribute("classes", classes);
        model.addAttribute("rules", rules);

        //TMP---------------------------
        System.out.println(classes);
        for (String cl : classes)
        {
            if (cl != "None")
                if (reader.getRulesNamesForClass(cl).isEmpty())
                    System.out.println(cl + " is empty!");
                else
                    System.out.println(cl + " is OK!");
        }
        //endTMP---------------------------

        return "index";
    }

    @RequestMapping("/filter")
    public String filter(Model model,  @RequestParam("filter") String filter)
    {
        SWRLReader reader = new SWRLReader(
                "/Users/krzysztof/IdeaProjects/SWRLAnalyzer/src/main/resources/family.xml");
        /*TODO: Tu wczytaj przefiltrowane metody do zmiennej 'rules'*/
        String[] rules = {"example"};






        if(filter.equals("None"))
        {
            rules = rulesMap.values().toArray(new String[0]);
            //rules = reader.getRules().toArray(new String[0]);
        } else {
            List<String> rulesNames = reader.getRulesNamesForClass(filter);
            List<String> rulesTmp = new ArrayList<String>();
            for (String rule : rulesNames) {
                //rules += rulesMap.get(rule);
                rulesTmp.add(rulesMap.get(rule));
            }
            rules = rulesTmp.toArray(new String[0]);
            System.out.println(rules);
            /*System.out.println("Filtr: " + filter + filter.length());
            rules = reader.getFilteredRules(filter).toArray(new String[0]);
            System.out.println("Nazwy regul: " + reader.getRulesNamesForClass(filter));
            System.out.println("Reguly: ");
            for (String rule : rules)
                System.out.println(rule);*/
        }

        model.addAttribute("rules", rules);
        return "swrl_list";
    }
}
