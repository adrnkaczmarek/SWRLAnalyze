package analyzer.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa reprezentuje informacje o wnioskowaniu, dotyczącym filtrowania reguł nazwami klas.
 */
public class SearchRulesSpy
{
    Map<String, List<String>> next_level;
    Map<String, List<String>> class_rule_map;

    /**
     * Konstruktor klasy SWRLReader.
     */
    SearchRulesSpy()
    {
        this.next_level = new HashMap<>();
        this.class_rule_map = new HashMap<>();
    }

    /**
     * Zwraca mapę klas (klucz) i reguł (wartość) aktualnie analizowanego poziomou hierarchi klas.
     * @return mapa klas (klucz) i reguł (wartość) aktualnie analizowanego poziomou hierarchi klas.
     */
    public Map<String, List<String>> getNextLevel()
    {
        return next_level;
    }

    /**
     * Ustawia mapę klas (klucz) i reguł (wartość) aktualnie analizowanego poziomou hierarchi klas.
     * @param next_level mapa klas (klucz) i reguł (wartość) aktualnie analizowanego poziomou hierarchi klas.
     */
    public void setNextLevel(Map<String, List<String>> next_level)
    {
        this.next_level = next_level;
    }

    /**
     * Zwraca mapę klas (klucz) i reguł (wartość), będącymi składowymi klas z zmiennej next_level.
     * @return mapa klas (klucz) i reguł (wartość), będącymi składowymi klas z zmiennej next_level.
     */
    public Map<String, List<String>> getClassRuleMap()
    {
        return class_rule_map;
    }

    /**
     * Ustawia mapę klas (klucz) i reguł (wartość), stanowiących będącymi składowymi klas z zmiennej next_level.
     * @param class_rule_map mapa klas (klucz) i reguł (wartość), będącymi składowymi klas z zmiennej next_level.
     */
    public void setClassRuleMap(Map<String, List<String>> class_rule_map)
    {
        this.class_rule_map = class_rule_map;
    }
}
