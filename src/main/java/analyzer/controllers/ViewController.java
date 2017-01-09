package analyzer.controllers;

import analyzer.utils.SWRLReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController
{
    private SWRLReader reader;

    @RequestMapping("/index")
    public String index(Model model)
    {
        reader = new SWRLReader(
                "/Users/krzysztof/IdeaProjects/SWRLAnalyzer/src/main/resources/family.xml");
        String[] rules = reader.getRules().toArray(new String[0]);
        String[] classes = reader.getClasses().toArray(new String[0]);
        model.addAttribute("classes", classes);
        model.addAttribute("rules", rules);
        return "index";
    }

    @RequestMapping("/filter")
    public String filter(Model model, String filter)
    {
        /*TODO: Tu wczytaj przefiltrowane metody do zmiennej 'rules'*/
        System.out.println("Filtr: " + filter);
        String[] rules = reader.getFilteredRules(filter).toArray(new String[0]);
        model.addAttribute("rules", rules);
        return "swrl_list";
    }
}
