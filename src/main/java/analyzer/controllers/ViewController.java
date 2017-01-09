package analyzer.controllers;

import analyzer.utils.SWRLReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController
{
    private SWRLReader reader;

    @RequestMapping("/index")
    public String index(Model model)
    {
        reader = new SWRLReader(
                "C:\\Users\\adrn.kaczmarek\\IdeaProjects\\SWRLAnalyze\\src\\main\\resources\\family.xml");
        String[] rules = this.reader.getRules().toArray(new String[0]);
        List<String> classes_list = this.reader.getClasses();
        classes_list.add(0, "None");
        String[] classes = classes_list.toArray(new String[0]);
        model.addAttribute("classes", classes);
        model.addAttribute("rules", rules);
        return "index";
    }

    @RequestMapping("/filter")
    public String filter(Model model,  @RequestParam("filter") String filter)
    {
        /*TODO: Tu wczytaj przefiltrowane metody do zmiennej 'rules'*/
        String[] rules = {"example"};

        if(filter.equals("None"))
        {

        }

        model.addAttribute("rules", rules);
        return "swrl_list";
    }
}
