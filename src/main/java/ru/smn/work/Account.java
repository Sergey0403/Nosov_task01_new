package ru.smn.work;

import java.util.*;

public class Account {
    String controlCurrency = "RUR,USD,EUR,CNY,BYR,TRY"; //разрешенные валюты
    Map<String, int[]> curSum = new HashMap<>();

    String nameOwner;
    private Stack<Command> undoStack = new Stack<>();

    public Account(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя владельца счета не может быть пустым!");
        this.nameOwner = name;
    }

    public Map<String, int[]> getCurSum() {
        return curSum;
    }

    /**
     * Выдача пары валюта-количество и имя владельца счета одной текстовой строкой
     */
    public String txtGetCurSum() {
        String txt = "{";
        String zap = "";
        for (Map.Entry<String, int[]> entry : curSum.entrySet()) {
            String key = entry.getKey();
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
    public String txtGetCurSum1() {
        String txt = "{";
        String zap = "";
        for (Map.Entry<String, int[]> entry : curSum.entrySet()) {
            String key = entry.getKey();
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
        Command cmd = new NewCommandAccount(name, this.nameOwner);
        execute(cmd);
        this.nameOwner = name;
    }
    /**
     * Редактируем счет и добавляем строку в стек undoStack для восстановления
     */
    public void editCurCount(String curr, int count) {
        String serror = "";
        if (controlCurrency.indexOf(curr) < 0) serror += "Валюты " + curr + " нет в списке разрешенных валют!";
        if (count < 0) serror += " Количество валюты не может быть отрицательным!";
        if (!serror.isEmpty()) {
            throw new IllegalArgumentException(serror);
        }
        Command cmd = new NewCommandAccount(curr, count, curSum);
        execute(cmd);
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
        this.undoStack= (Stack)memento.undoStack.clone();
    }
    final class NewCommandAccount implements Command {
        private String curr;  //вводимая валюта
        private int count;  //вводимое количество валюты
        private int[] counts = new int[2];  //массив "Количество (новое, старое)"
        private Map<String, int[]> curSum = new HashMap<>();  //мапа "Валюта"-"Количество (новое, старое)"
        private String name;  //вводимое имя владельца счета
        private String nameOld;  //имя владельца счета старое
        private String flag;

        public NewCommandAccount(String curr, int count, Map<String, int[]> curSum) {
            this.curr = curr;
            this.count = count;
            this.curSum = curSum;
            this.flag = "curSum";  //признак изменения пары валюта-количество
        }

        public NewCommandAccount(String name, String nameOld) {
            this.name = name;
            this.nameOld = nameOld;
            this.flag = "name";  //признак изменения пары валюта-количество
        }

        public int editCurCount() {
            int[] prevCount = new int[2];
            prevCount = curSum.get(curr);   //предыдущее значение
            if (prevCount != null) {
                counts[1] = prevCount[0]; //запомним предыдущее значение для восстановления
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

        public void editName() {
        }

        public void undoEditName() {
            String txt = this.name;
            nameOwner = this.nameOld;
            this.nameOld = txt;
        }

        @Override
        public void execute() {
            switch (flag) {
                case ("curSum"):
                    editCurCount();
                    break;
                case ("name"):
                    editName();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void undo() {
            switch (flag) {
                case ("curSum"):
                    undoEditCurCount();
                    break;
                case ("name"):
                    undoEditName();
                    break;
                default:
                    break;
            }
        }
    }
}
