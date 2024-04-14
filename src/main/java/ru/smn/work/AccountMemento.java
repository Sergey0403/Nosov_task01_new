package ru.smn.work;

import java.util.*;

public class AccountMemento {
    public Map<Account.Curr, int[]> curSum = new HashMap<>();
    public String nameOwner;
    public ArrayDeque<Command> undoStack = new ArrayDeque<>();

    /**
     * Запомним состояние счета
     */
    public AccountMemento(Map<Account.Curr, int[]> curSum,
                          String nameOwner,
                          ArrayDeque<Command> undoStack) {
        this.curSum = new HashMap<>();
        this.undoStack = new ArrayDeque<>();
        copySumCount(curSum, this.curSum);  //копируем мапу, которую запоминаем
        this.nameOwner = nameOwner;
        this.undoStack = undoStack.clone();
    }

    /**
     * Копируем мапу в новую..
     */
    public void copySumCount(Map<Account.Curr, int[]> mapFrom,
                             Map<Account.Curr, int[]> mapTo) {
        for (Map.Entry<Account.Curr, int[]> entry : mapFrom.entrySet()) {
            int[] cnt = Arrays.copyOf(entry.getValue(), entry.getValue().length);
            mapTo.put(entry.getKey(), cnt);
        }
    }
}
