package calculator;

import operation.AbstractOperationStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Calculator {

    /**
     * 表示待输入首个数字
     */
    private static final int INIT = 0;

    /**
     * 表示待输入运算符
     */
    private static final int WAIT_OPERATOR = 1;

    /**
     * 表示待输入第二个数字
     */
    private static final int WAIT_SECOND = 2;

    /**
     * 记录当前操作状态
     */
    private int operateFlag = INIT;

    /**
     * 记录当前的加减乘除操作
     */
    private String currentOperator;

    /**
     * 运算的第一个数
     */
    private BigDecimal firstNum;

    private final ResultStack resultStack = new ResultStack();

    /**
     * 内部类，实现操作记录的回退和重做
     */
    private class ResultStack extends AbstractOperationStack<BigDecimal> {

        /**
         * 永远指向 undo 操作需要返回的值的下标
         */
        private int undoIndex = -1;

        /**
         * 永远指向 redo 操作需要返回的值的下标
         */
        private int redoIndex = 1;

        /**
         * redo 操作需要返回的值的下标的最大位置
         */
        private int maxRedoIndex = 1;

        /**
         * 纪录 redo 和 undo 结果值的数组，初始值固定为 0
         */
        private final ArrayList<BigDecimal> numArray = new ArrayList<BigDecimal>() {
            {
                add(BigDecimal.ZERO);
            }
        };

        protected BigDecimal undo() {
            if (undoIndex < 0) {
                operateFlag = INIT;
                return null;
            }

            BigDecimal result = numArray.get(undoIndex);
            // 将 undo 的下标移动到下一次 undo 操作时使用的下标
            undoIndex--;
            redoIndex--;
            firstNum = result;
            if (undoIndex == -1) {
                operateFlag = INIT;
            } else {
                operateFlag = WAIT_OPERATOR;
            }
            return result;
        }

        protected BigDecimal redo() {
            if (redoIndex == maxRedoIndex) {
                return null;
            }

            BigDecimal result = numArray.get(redoIndex);
            // 将 redo 的下标移动到下一次 redo 操作时使用的下标
            undoIndex++;
            redoIndex++;

            firstNum = result;
            operateFlag = WAIT_OPERATOR;
            return result;
        }

        /**
         * 每次计算操作都记录结果值
         * 并触发 undo 和 redo 的下标刷新
         */
        protected void recordResult(BigDecimal result) {
            numArray.add(redoIndex, result);

            undoIndex++;
            redoIndex++;
            maxRedoIndex = redoIndex;

            firstNum = result;
        }
    }

    private BigDecimal calculate(BigDecimal lastNum) {
        BigDecimal result = null;
        switch (currentOperator) {
            case NumChecker.ADD:
                result = firstNum.add(lastNum);
                break;
            case NumChecker.SUBTRACT:
                result = firstNum.subtract(lastNum);
                break;
            case NumChecker.MULTIPLY:
                result = firstNum.multiply(lastNum);
                break;
            case NumChecker.DIVIDE:
                result = firstNum.divide(lastNum, 10, RoundingMode.HALF_DOWN);
                break;
        }
        resultStack.recordResult(result);
        operateFlag = 1;
        return result;
    }

    private void recordOperate(String operator) {
        currentOperator = operator;
        operateFlag++;
    }

    private void recordFirstNum(BigDecimal f) {
        firstNum = f;
        resultStack.recordResult(firstNum);
        operateFlag++;
    }

    public void run() {
        System.out.println("回退操作请输入 undo ，按回车确认。 undo 将直接回退到上一个运算结果");
        System.out.println("重做操作请输入 redo ，按回车确认。 redo 将直接重做到下一个运算结果");
        System.out.println("退出请输入 exit ，并按回车确认");
        System.out.println("请输入您的首个运算数字：");

        //创建一个扫描器对象，用于接收键盘数据
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String in = scanner.next();

            // 处理退出操作
            if (in.equals("exit")) {
                break;
            }

            // 处理 undo 操作
            if (in.equals("undo")) {
                BigDecimal result = resultStack.undo();
                if (null == result) {
                    System.out.println("当前已经无 undo 内容，请输入 redo 或输入第一个运算数字：");
                    continue;
                }

                System.out.println("结果：" + result.toPlainString());
                System.out.println("请继续您的运算操作：");
                continue;
            }

            // 处理 redo 操作
            if (in.equals("redo")) {
                BigDecimal result = resultStack.redo();
                if (null == result) {
                    System.out.println("当前已经无 redo 内容，请输入 undo 或输入运算符：");
                    continue;
                }

                System.out.println("结果：" + result.toPlainString());
                System.out.println("请继续您的运算操作：");
                continue;
            }

            if (operateFlag == INIT) {
                if (!NumChecker.isNumeric(in)) {
                    System.out.println("输入有误，不是一个数字，请重新输入：");
                    continue;
                }

                recordFirstNum(new BigDecimal(in));
                System.out.println("请输入您的运算符号，支持 + - * /：");
                continue;
            }

            if (operateFlag == WAIT_OPERATOR) {
                if (!NumChecker.isOperator(in)) {
                    System.out.println("输入有误，不是一个运算符，请重新输入：");
                    continue;
                }

                recordOperate(in);
                System.out.println("请输入您的下个运算数字：");
                continue;
            }

            if (operateFlag == WAIT_SECOND) {
                if (!NumChecker.isNumeric(in)) {
                    System.out.println("输入有误，不是一个数字，请重新输入：");
                    continue;
                }

                BigDecimal num = new BigDecimal(in);
                if (num.equals(BigDecimal.ZERO) && currentOperator.equals(NumChecker.DIVIDE)) {
                    System.out.println("输入有误，被除数不能为 0 ，请重新输入：");
                    continue;
                }

                BigDecimal result = calculate(num);
                System.out.println("结果：" + result.toPlainString());
                System.out.println("请继续您的运算操作：");
            }

        }

        scanner.close();
    }
}
