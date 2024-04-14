package ru.smn.work;

import java.util.*;

public class Account {
    static public enum Curr {RUR, USD, EUR, CNY, BYR, TRY};

    String nameOwner;
    private ArrayDeque<Command> undoStack = new ArrayDeque<>();

    Map<Curr, int[]> curSum = new HashMap<>();

    public Account(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя владельца счета не может быть пустым!");
        this.nameOwner = name;
    }

    /**
     * Редактируем счет и добавляем строку в стек undoStack для восстановления
     */
    public void editCurCount(String currency, int count) {
        Curr curr;
        String serror = "";

        if (currency == null) {
            throw new NullPointerException("Error - Передали нулевой параметр по валюте!");
        }
        try {
            curr = Curr.valueOf(currency);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error - Валюта " + currency + " не входит в список разрешенных!");
        }
        if (count < 0) serror += " Количество валюты не может быть отрицательным!";
        if (!serror.isEmpty()) {
            throw new IllegalArgumentException(serror);
        }
        Command cmd = new NewCommandAccountMap(curr, count, curSum);
        execute(cmd);
    }

    public Map<Curr, int[]> getCurSum() {
        // Map<String, int[]> curSumClone = new HashMap<>();
        Map<Curr, int[]> curSumClone = new HashMap<>();
        curSumClone.putAll(curSum);
        return curSumClone;
    }

    /**
     * Выдача пары валюта-количество и имя владельца счета одной текстовой строкой
     */
    public String txtGetCurSum() {
        String txt = "{";
        String zap = "";
        for (Map.Entry<Curr, int[]> entry : curSum.entrySet()) {
            Curr key = entry.getKey();
            int[] val = curSum.get(key);
            String v0 = String.valueOf(val[0]);  //values[0];
            String v1 = String.valueOf(val[1]);  //values[0];
            txt += zap + key + " " + v0 + " " + v1;
            zap = ", ";
        }
        txt += " " + nameOwner + "}";
        return txt;
    }

    /**
     * Выдача пары валюта-количество (без предыдущего значения количества) в текстовом виде
     */
    public String txtGetcurSum1() {
        String txt = "{";
        String zap = "";
        for (Map.Entry<Curr, int[]> entry : curSum.entrySet()) {
            Curr key = entry.getKey();
            int[] val = curSum.get(key);
            String v0 = String.valueOf(val[0]);  //values[0];
            String v1 = String.valueOf(val[1]);  //values[0];
            txt += zap + key + " " + v0;
            zap = ", ";
        }
        txt += "}";
        return txt;
    }

    public String getName() {
        return nameOwner;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя владельца счета не может быть пустым!");
        Command cmd = new NewCommandAccountName(name, this.nameOwner);
        execute(cmd);
        this.nameOwner = name;
    }

    /**
     * ------- for undo ---------------------------------------------------
     */
    void execute(Command cmd) {
        undoStack.push(cmd);
        cmd.execute();
    }

    public void undo() {
        if (undoEmpty())
            throw new RuntimeException("Undo невозможно! Стек пустой!");
        Command cmd = undoStack.pop();
        cmd.undo();
    }

    /**
     * Возващает true при пустом стеке откатов undoStack
     */
    public boolean undoEmpty() {
        return undoStack.isEmpty();
    }

    /**
     * Запомним состояние счета
     */
    public AccountMemento saveState() {
        System.out.println("Сохранение счета");
        return new AccountMemento(curSum, nameOwner, undoStack);
    }

    /**
     * Восстановим состояние счета
     */
    public void restoreState(AccountMemento memento) {
        System.out.println("Восстановление счета");
        this.curSum = new HashMap<>();
        memento.copySumCount(memento.curSum, this.curSum);
        this.nameOwner = memento.nameOwner;
        this.undoStack = memento.undoStack.clone();
    }

    final class NewCommandAccountMap implements Command {
        private Curr curr;  //вводимая валюта
        private int count;  //вводимое количество валюты
        private int[] counts = new int[2];  //массив "Количество (новое, старое)"
        private Map<Curr, int[]> curSum = new HashMap<>();  //мапа "Валюта"-"Количество (новое, старое)"

        public NewCommandAccountMap(Curr curr, int count, Map<Curr, int[]> curSum) {
            this.curr = curr;
            this.count = count;
            this.curSum = curSum;
        }

        public int editCurCount() {

            int[] prevCount = new int[2];

            try {
                prevCount = curSum.get(curr);   //предыдущее значение
                counts[1] = prevCount[0]; //запомним предыдущее значение для восстановления
            } catch (Exception e) {
            }
            counts[0] = count;
            curSum.put(curr, counts);
            return counts[1];   //возвращаем предыдущее значение

        }

        public int undoEditCurCount() {
            int prev = counts[1];   //предыдущее значение
            if (prev != 0) {  //ранее была запись по этой валюте
                counts[1] = counts[0];
                counts[0] = prev;
                curSum.put(curr, counts);
            } else {   //записи по этойвалюте не было ранее
                curSum.remove(curr);
            }
            return prev;   //возвращаем предыдущее значение
        }

        @Override
        public void execute() {
            editCurCount();
        }

        @Override
        public void undo() {
            undoEditCurCount();
        }
    }

    final class NewCommandAccountName implements Command {
        private String name;  //вводимое имя владельца счета
        private String nameOld;  //имя владельца счета старое

        public NewCommandAccountName(String name, String nameOld) {
            this.name = name;
            this.nameOld = nameOld;
        }

        public void editName() {
        }

        public void undoEditName() {
            String txt = this.name;
            nameOwner = this.nameOld;
            this.nameOld = txt;
        }

        @Override
        public void execute() {
            editName();
        }

        @Override
        public void undo() {
            undoEditName();
        }
    }
}
