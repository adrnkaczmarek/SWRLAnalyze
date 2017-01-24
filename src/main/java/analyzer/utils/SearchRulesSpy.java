package analyzer.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchRulesSpy
{
    Map<String, List<String>> next_level;
    Map<String, List<String>> class_rule_map;

    public SearchRulesSpy( Map<String, List<String>> next_level, Map<String, List<String>> class_rule_map)
    {
        this.next_level = next_level;
        this.class_rule_map = class_rule_map;
    }

    SearchRulesSpy()
    {
        this.next_level = new HashMap<>();
        this.class_rule_map = new HashMap<>();
    }

    public Map<String, List<String>> getNextLevel()
    {
        return next_level;
    }

    public void setNextLevel(Map<String, List<String>> next_level)
    {
        this.next_level = next_level;
    }

    public Map<String, List<String>> getClassRuleMap()
    {
        return class_rule_map;
    }

    public void setClassRuleMap(Map<String, List<String>> class_rule_map)
    {
        this.class_rule_map = class_rule_map;
    }
}
