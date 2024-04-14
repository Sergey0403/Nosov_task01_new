package ru.smn.work;

public class Main {
    public static void main(String[] args) {

        Account acc = new Account("Sergey");
        acc.editCurCount("USD", 1001);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.setName("Vova");
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());

        acc.editCurCount("RUR", 1002);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.editCurCount("RUR", 2002);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.editCurCount("EUR", 1003);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.editCurCount("EUR", 3006);
        System.out.println("  do: " + acc.txtGetCurSum());

        AccountMemento accMem = acc.saveState();   //запомним состояние счета
        System.out.println("1mem: " + acc.txtGetCurSum());

        acc.editCurCount("TRY", 1004);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.editCurCount("CNY", 1005);
        System.out.println("  do: " + acc.txtGetCurSum());

        acc.setName("Захар Загадкин");
        System.out.println("  do: " + acc.txtGetCurSum());

        acc.editCurCount("BYR", 1006);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());

        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());

        acc.restoreState(accMem);   //восстановление запомненного состояние счета
        System.out.println("2mem: " + acc.txtGetCurSum());

        acc.editCurCount("RUR", 4002);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.editCurCount("BYR", 4006);
        System.out.println("  do: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());
        acc.undo();
        System.out.println("undo: " + acc.txtGetCurSum());

        System.out.println(" mem: " + acc.txtGetCurSum());
        System.out.println(acc.getName());
        acc.editCurCount("RUR", 2002);
        acc.setName("Vova");
        System.out.println("  do: " + acc.txtGetCurSum());
        System.out.println(acc.getName());

    }
}