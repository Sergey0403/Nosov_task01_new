package ru.smn.work;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AccountMemento {
    public Map<String, int[]> curSum = new HashMap<>();
    public String nameOwner;
    public Stack<Command> undoStack = new Stack<>();

    /**
     * Запомним состояние счета
     */
    public AccountMemento(Map<String, int[]> curSum,
                          String nameOwner,
                          Stack<Command> undoStack) {
        this.curSum = new HashMap<>();
        this.undoStack = new Stack<>();
        copySumCount(curSum, this.curSum);  //копируем мапу, которую запоминаем
        this.nameOwner = nameOwner;
        this.undoStack = (Stack) undoStack.clone();
    }

    /**
     * Копируем мапу в новую..
     */
    public void copySumCount(Map<String, int[]> mapFrom,
                             Map<String, int[]> mapTo) {
        for (Map.Entry<String, int[]> entry : mapFrom.entrySet()) {
            int[] cnt = Arrays.copyOf(entry.getValue(), entry.getValue().length);
            mapTo.put(entry.getKey(), cnt);
        }
    }
}
