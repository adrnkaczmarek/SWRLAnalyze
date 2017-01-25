package analyzer.controllers;

import analyzer.utils.SWRLReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Klasa będąca głównym kontrolerem widoku.
 * @author Adrian Kaczmarek Krzysztof Rózga Radosław Kapłon
 * @version 1.0
 */
@Controller
public class ViewController
{
    private String filePath = "C:\\Users\\adrn.kaczmarek\\IdeaProjects\\SWRLAnalyze\\src\\main\\resources\\family.xml";
    private Map<String, String> rulesMap;

    /**
     * Funkcja zwracająca główny widok aplikacji, wraz z wszsytkimi regułami.
     * @param model
     * @return widok główny z wczytanymi danymi.
     */
    @RequestMapping("/index")
    public String index(Model model)
    {
        SWRLReader reader = new SWRLReader(filePath);
        rulesMap = reader.getRules();
        String[] rules = rulesMap.values().toArray(new String[0]);
        String[] rulesNames = rulesMap.keySet().toArray(new String[0]);
        List<String> classes_list = reader.getClasses();
        classes_list.add(0, "None");
        String[] classes = classes_list.toArray(new String[0]);
        model.addAttribute("classes", classes);
        model.addAttribute("rules", rules);
        model.addAttribute("rulesNames", rulesNames);

        return "index";
    }

    /**
     * Funkcja odpowiada za wykonanie wybranych filtracji i zwrócenie jej wyników.
     * @param model
     * @param filter reguła będąca argumentem filtracji
     * @return widok z wczytanymi danymi.
     */
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
            rules = getRulesFromMap(reader.getRulesNamesForClass(filter));
        }

        model.addAttribute("current_level", reader.getSpy().getClassRuleMap());
        model.addAttribute("next_level", reader.getSpy().getNextLevel());
        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    /**
     *  Funkcja odpowiada za filtrowanie reguł jako obiektów funkcyjnych.
     * @param model
     * @return widok z wczytanymi danymi.
     */
    @RequestMapping("/filterFunctional")
    public String filterAsFunctionalProperty(Model model)
    {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules = getRulesFromMap(reader.getFunctionalObjectProperty());
        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    /**
     * Funkcja odpowiada za filtrowanie reguł jako symetrycznych obiektów.
     * @param model
     * @return widok z wczytanymi danymi.
     */
    @RequestMapping("/filterSymmetric")
    public String filterAsSymmetricProperty(Model model)
    {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules = getRulesFromMap(reader.getSymmetricObjectProperty());
        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    /**
     * Funkcja odpowiada za filtrowanie reguł jako podobiektów.
     * @param model
     * @param rule reguła będąca argumentem filtracji
     * @return widok z wczytanymi danymi.
     */
    @RequestMapping("/filterAsSubOfProperty")
    public String filterAsSubOfProperty(Model model, @RequestParam("rule") String rule)
    {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules = getRulesFromMap(reader.getSubOfObjectProperty(rule));
        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    /**
     * Funkcja odpowiada za filtrowanie reguł jako odwrotnych obiektów.
     * @param model
     * @param rule reguła będąca argumentem filtracji
     * @return widok z wczytanymi danymi.
     */
    @RequestMapping("/filterAsInverseProperty")
    public String filterAsInverseProperty(Model model, @RequestParam("rule") String rule)
    {
        SWRLReader reader = new SWRLReader(filePath);
        String[] rules = getRulesFromMap(reader.getInverseObjectProperty(rule));
        if (rules.length == 0)
        {
            SWRLReader reader_for_inverse = new SWRLReader(filePath);
            rules = getRulesFromMap(reader_for_inverse.getInverseProperty(rule));
        }
        model.addAttribute("rules", rules);
        return "swrl_list";
    }

    /**
     * Funkcja pomocnicza odpowiadjąca za zwrócenie listy reguł z mapy, na podstawie nazw przekazanych jako argument.
     * @param rulesNames lista nazw reguł
     * @return lista reguł.
     */
    private String[] getRulesFromMap(List<String> rulesNames)
    {
        List<String> rulesTmp = new ArrayList<>();

        for (String rule : rulesNames)
        {
            if (rulesMap.containsKey(rule))
                rulesTmp.add(rulesMap.get(rule));
            else
                rulesTmp.add(rule + "()");
        }

        return rulesTmp.toArray(new String[0]);
    }
}
