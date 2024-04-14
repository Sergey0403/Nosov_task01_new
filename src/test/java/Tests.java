import org.junit.jupiter.api.Test;
import ru.smn.work.Account;
import ru.smn.work.AccountMemento;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
    @Test
    public void testNameOk() {
        String name1 = "Sergey";
        String name2;
        Account acc = new Account(name1);
        name2 = acc.getName();
        if (name1 != name2) {
            throw new RuntimeException("1-test error - Не ввелось имя владельца счета в конструкторе!");
        }
        name1 = "Alexander";
        acc.setName(name1);
        name2 = acc.getName();
        if (name1 != name2) {
            throw new RuntimeException("1-test error - Не ввелось имя владельца счета!");
        }
    }

    @Test
    public void testNameNotOk() {
        String name1 = "";
        String name2 = "";
        Boolean serror = true;
        Account acc;
        try {
            acc = new Account(name1);
        } catch (Exception e) {
            serror = false;   //законное исключение!
        }
        if (serror) {
            throw new RuntimeException("2-test error - Разрешено пустое имя в конструкторе!");
        }
        name1 = "Alexander";
        acc = new Account(name1);
        name1 = "";
        try {
            acc.setName(name1);
        } catch (Exception e) {
            // System.out.println("2-catch2");
        }
        name2 = acc.getName();
        if (name2.isEmpty()) {
            throw new RuntimeException("2-test error - Разрешено ввести пустое имя!");
        }
    }

    @Test
    public void testEditCurCountOk() {
        String name1 = "Sergey";
        Map<String, int[]> mCurCount;
        Account acc = new Account(name1);
        try {
            acc.editCurCount("EUR", 1003);
            mCurCount = acc.getCurSum();
            if (mCurCount.get("EUR")[0] != 1003) {
                throw new RuntimeException("3-test error - Не записались данные пары валюта-количество!");
            }
        } catch (Exception e) {
            throw new RuntimeException("3-test error - Ошибка при записи пары валюта-количество!");
        }
    }

    @Test
    public void testEditCurCountNotOk() {
        String name1 = "Sergey";
        Map<String, int[]> mCurCount;
        Account acc = new Account(name1);
        Boolean serror = true;

        try {
            acc.editCurCount("USD", -2222);
        } catch (Exception e) {
            serror = false;
        }
        mCurCount = acc.getCurSum();
        if (serror || mCurCount.size() > 0 && mCurCount.get("USD")[0] == -2222) {
            throw new RuntimeException("5-test error - Разрешен ввод отрицательного количества!");
        }
    }

    @Test
    public void testUndoNameOk() {
        String name1 = "Sergey";
        String name3 = "Vladimir";
        Account acc = new Account(name1);
        acc.setName(name3);
        acc.undo();
        assertEquals(name1, acc.getName());
    }

    @Test
    public void testUndoCurSumOk() {
        String name1 = "Sergey";
        String cur1;
        String cur2;
        String cur3;
        String cur4;
        Account acc = new Account(name1);
        acc.editCurCount("USD", 1001);
        cur1 = acc.txtGetCurSum1();
        acc.editCurCount("USD", 2001);
        cur2 = acc.txtGetCurSum1();
        acc.editCurCount("RUR", 1002);
        acc.undo();
        cur3 = acc.txtGetCurSum1();
        assertEquals(cur2, cur3);
        acc.undo();
        cur4 = acc.txtGetCurSum1();
        assertEquals(cur1, cur4);
    }

    @Test
    public void testUndoEmpty() {
        String name1 = "Sergey";
        Boolean serror = true;
        Account acc = new Account(name1);
        acc.editCurCount("USD", 1001);
        acc.undo();
        try {
            acc.undo();
        } catch (Exception e) {
            serror = false;
        }
        if (serror) {
            throw new RuntimeException("5-test error - Нет реакции на пустой стек откатов undo!");
        }
    }

    @Test
    public void testMemento() {
        String name1 = "Sergey";
        String cur1;
        String cur2;
        String cur3;
        String cur4;
        Account acc = new Account(name1);
        acc.editCurCount("USD", 1001);
        acc.editCurCount("RUR", 1002);
        cur1 = acc.txtGetCurSum1();

        AccountMemento accMem = acc.saveState();   //запомним состояние счета

        acc.editCurCount("USD", 2001);
        acc.editCurCount("BYR", 1004);
        cur2 = acc.txtGetCurSum1();

        acc.restoreState(accMem);   //восстановление запомненного состояние счета

        cur3 = acc.txtGetCurSum1();
        assertEquals(cur1, cur3);
    }

}
