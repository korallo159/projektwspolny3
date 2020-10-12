package jbwm.jbwm;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Zbiór Funkcji przydatnych w programie
 *
 */
public final class Func {
    /**
     * @param tab tablica
     * @return ostatni element tablicy
     */
    public static <T> T ostatni(T[] tab) {
        if (tab.length == 0) return null;
        return tab[tab.length-1];
    }

    /**
     * Zamienia liste na Stringa
     *
     * @param lista lista
     * @param start pierwszy używany element
     * @param wstawka rozdzielacz
     *
     * @return elementy listy począwczy od indexu start rodzielone wstawką
     */
    public static String listToString(List<?> lista, int start, String wstawka) {
        StringBuilder s = new StringBuilder(lista.size() > start ? ""+lista.get(start) : "");
        int i=0;
        for (Object obj : lista)
            if (i++ > start)
                s.append(wstawka).append(obj == null ? null : obj.toString());
        return s.toString();
    }
    public static String listToString(Object[] lista, int start) {
        return listToString(Lists.newArrayList(lista), start, " ");
    }
    public static String listToString(Object[] lista, int start, String wstawka) {
        return listToString(Lists.newArrayList(lista), start, wstawka);
    }
    public static String listToString(List<?> lista, int start) {
        return listToString(lista, start, " ");
    }

}
