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
    private String filePath = "C:\\Users\\adrn.kaczmarek\\IdeaProjects\\SWRLAnalyze\\src\\main\\resources\\family.xml";
    private Map<String, String> rulesMap;


    @RequestMapping("/index")
    public String index(Model model)
    {
        SWRLReader reader = new SWRLReader(filePath);
        rulesMap = reader.getRules();
        String[] rules = rulesMap.values().toArray(new String[0]);
        List<String> classes_list = reader.getClasses();
        classes_list.add(0, "None");
        String[] classes = classes_list.toArray(new String[0]);
        model.addAttribute("classes", classes);
        model.addAttribute("rules", rules);

        return "index";
    }

    @RequestMapping("/filter")
    public String filter(Model model, @RequestParam("filter") String filter)
    {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules;

        if(filter.equals("None"))
        {
            rules = rulesMap.values().toArray(new String[0]);
        }
        else
        {
            List<String> rulesNames = reader.getRulesNamesForClass(filter);
            List<String> rulesTmp = new ArrayList<>();

            for (String rule : rulesNames)
            {
                rulesTmp.add(rulesMap.get(rule));
            }
            rules = rulesTmp.toArray(new String[0]);
        }

        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    @RequestMapping("/filterFunctional")
    public String filterFunctional(Model model) {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules;

        List<String> rulesNames = reader.getFunctionalObjectProperty();
        List<String> rulesTmp = new ArrayList<>();
        for (String rule : rulesNames) {
            if (rulesMap.containsKey(rule))
                rulesTmp.add(rulesMap.get(rule));
            else
                rulesTmp.add(rule + "()");
        }
        rules = rulesTmp.toArray(new String[0]);

        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    @RequestMapping("/filterSymmetric")
    public String filterSymmetric(Model model) {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules;

        List<String> rulesNames = reader.getSymmetricObjectProperty();
        List<String> rulesTmp = new ArrayList<>();
        for (String rule : rulesNames) {
            if (rulesMap.containsKey(rule))
                rulesTmp.add(rulesMap.get(rule));
            else
                rulesTmp.add(rule + "()");
        }
        rules = rulesTmp.toArray(new String[0]);

        model.addAttribute("rules", rules);
        return "swrl_list";
    }
}
