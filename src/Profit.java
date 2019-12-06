import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;

public class Profit {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Для справки введите \"h\" или");

        System.out.println("Введите стоимость предмета или предметов");

        String costs = in.nextLine();

//        BigInteger a = new BigInteger(costs);
//
//        System.out.println(a);
//        System.exit(0);

        if (checkOptionalFunction(costs, in, args)) {

            return;
        }

//        BigInteger[] costsArray = getCostsArray(costs, in, args);

        long[] costsArray = getCostsArray(costs, in, args);

        if (costsArray == null) {

            return;
        }

        System.out.println("Если сумма продажи предмета(ов) отличается от стоимости введите \"+\"");

        System.out.println("Если нет введите \"-\"");

        String text = in.nextLine();

        if (checkOptionalFunction(text, in, args)) {

            return;
        }

        if (!text.equals("+") && !text.equals("-")) {

            if (checkInputError(in, args, "Ошибка ввода")) {

                return;
            }
        }

        if (text.equals("+")) {

            System.out.println("Укажите позицию(и)");

            String positions = in.nextLine();

            if (checkOptionalFunction(positions, in, args)) {

                return;
            }

            Set<Integer> positionsArray = getPositionsArray(positions, in, args);

            if (positionsArray == null) {

                return;
            }

            Boolean check = checkPositions(costsArray, positionsArray);

            if (check == null) {

                if (checkInputError(in, args, "Количество позиций превышает количество товаров")) {

                    return;
                }

                return;
            }

            if (!check) {

                if (checkInputError(in, args, "Позиция не найдена")) {

                    return;
                }
            }

            System.out.println("Укажите сумму(ы)");

            String amounts = in.nextLine();

            if (checkOptionalFunction(amounts, in, args)) {

                return;
            }

            long[] amountsArray = getArrayNumbers(amounts, in, args);

            if (amountsArray == null) {

                return;
            }

            long[] newAmountsArray = getNewAmountsArray(costsArray, positionsArray, amountsArray);

            if (newAmountsArray == null) {

                if (checkInputError(in, args, "Не указаны все цены для позиций")) {

                    return;
                }

                return;
            }

            Boolean checkAmountsArray = checkNewAmounts(costsArray, positionsArray, amountsArray, newAmountsArray);

            if (checkAmountsArray == null) {

                if (checkInputError(in, args, "Количество сумм превышает количество позиций")) {

                    return;
                }

                return;
            }

            if (!checkAmountsArray) {

                if (checkInputError(in, args, "Сумма продажи не может быть меньше стоимости предмета")) {

                    return;
                }
            }

            String response = getResponse(in, args);

            if (response == null) {

                return;
            }

            calculationOfProfit(costsArray, newAmountsArray, positionsArray, response.equals("+"));

            checkInputError(in, args, null);

        } else {

            String response = getResponse(in, args);

            if (response == null) {

                return;
            }

            calculationOfProfit(costsArray, response.equals("+"));

            checkInputError(in, args, null);
        }
    }

    private static void calculationOfProfit(long[] costsArray, long[] newAmountsArray, Set<Integer> positionsArray, boolean resp) {

        long result = 0;

        long bonus = 0;

        for (int i = 0; i < costsArray.length; i++) {

            long cos = costsArray[i];

            for (int pos : positionsArray) {

                if (i == pos) {

                    long amount = newAmountsArray[i];

                    long res = amount - cos;

                    bonus = res / 100 * 20;
                }
            }

            long sum = (cos + bonus) / 100 * 70;

            result += sum;
        }

        if (resp) {

            result += result / 100 * 30;
        }

        System.out.println("Ваша прибыль = " + result);
    }

    private static void calculationOfProfit(long[] costsArray, boolean resp) {

        long result = 0;

        for (long cos : costsArray) {

            long sum = cos / 100 * 70;

            result += sum;
        }

        if (resp) {

            result += result / 100 * 30;
        }

        System.out.println("Ваша прибыль = " + result);
    }

    private static long[] getNewAmountsArray(long[] costsArray, Set<Integer> positionsArray, long[] amountsArray) {

        if (amountsArray.length < positionsArray.size()) {
            return null;
        }

        long[] newAmountsArray = new long[costsArray.length];

        int index = 0;

        for (int pos : positionsArray) {

            for (int i = 0; i < newAmountsArray.length; i++) {

                if (pos == i) {

                    newAmountsArray[i] = amountsArray[index];

                    index++;
                }
            }
        }

        return newAmountsArray;
    }

    private static Boolean checkPositions(long[] costsArray, Set<Integer> positionsArray) {

        if (positionsArray.size() > costsArray.length) {

            return null;
        }

        boolean check = false;

        for (long pos : positionsArray) {

            for (int i = 0; i < costsArray.length; i++) {

                if (pos != i) {

                    check = false;

                } else {

                    if (!check) {

                        check = true;
                    }

                    break;
                }
            }
        }

        return check;
    }

    private static Boolean checkNewAmounts(long[] costsArray, Set<Integer> positionsArray, long[] amountsArray, long[] newAmountsArray) {

        if (amountsArray.length > positionsArray.size()) {

            return null;
        }

        for (int pos : positionsArray) {

            long cos = costsArray[pos];

            long amount = newAmountsArray[pos];

            if (amount < cos) {

                return false;
            }
        }

        return true;
    }

    private static long[] getCostsArray(String costs, Scanner in, String[] args) {

        long[] costsArray = getArrayNumbers(costs, in, args);

        if (costsArray == null) {

            return null;
        }

        for (int i = 0; i < costsArray.length; i++) {

            System.out.println("Позиция " + i + " : " + costsArray[i]);
        }

        return costsArray;
    }

    private static long[] getArrayNumbers(String str, Scanner in, String[] args) {

        if (!checkByPattern(str)) {

            if (checkInputError(in, args, "Строка не число")) {

                return null;
            }
        }

        List<String> numStr = getListString(str);

        long[] numArr = new long[numStr.size()];

        for (int i = 0; i < numStr.size(); i++) {

            numArr[i] = Long.parseLong(numStr.get(i));

            if (numArr[i] == 0) {

                if (checkInputError(in, args, "Число должно быть больше нуля")) {

                    return null;
                }
            }
        }

        return numArr;
    }

    private static Set<Integer> getPositionsArray(String str, Scanner in, String[] args) {

        if (checkByPattern(str)) {

            List<String> listNumStr = getListString(str);

            Set<Integer> setInt = new HashSet<Integer>();

            for (String numStr : listNumStr) {

                setInt.add(Integer.parseInt(numStr));
            }

            return setInt;
        }

        if (checkInputError(in, args, "Строка не число")) {

            return null;
        }

        return null;
    }

    private static List<String> getListString(String str) {

        str = str.replaceAll("[^0-9]+", " ");

        return Arrays.asList(str.trim().split(" "));
    }

    private static boolean checkByPattern(String str) {

        Pattern pattern = Pattern.compile("\\d+");

        Matcher matcher = pattern.matcher(str);

        return matcher.find();
    }

    private static String getResponse(Scanner in, String[] args) {

        System.out.println("Если считать с комфортом введите " + "\"+\"");

        System.out.println("Если считать без комфорта введите " + "\"-\"");

        String response = in.nextLine();

        if (checkOptionalFunction(response, in, args)) {

            return null;
        }

        if (!response.equals("+") && !response.equals("-")) {

            if (checkInputError(in, args, "Ошибка ввода")) {

                return null;
            }
        }

        return response;
    }

    private static boolean checkGettingHelp(String str, Scanner in, String[] args) {

        if (getHelp(str)) {

            String resp = in.nextLine();

            if (getOptionalFunction(resp, args)) {

                return true;

            } else {

                return checkInputError(in, args, "Ошибка ввода");
            }
        }

        return false;
    }

    private static boolean getHelp(String str) {

        if (str.equals("h")) {

            System.out.println("Программа была создана для подсчёта прибыли с аукциона в игре black desert");
            System.out.println("В программу можно ввести только целочисленные значения не меньше нуля");
            System.out.println("Для перезапуска программы введите \"res\"");
            System.out.println("Для выхода из программы введите \"exit\"");

            return true;
        }

        return false;
    }

    private static boolean getOptionalFunction(String str, String[] args) {

        if (str.equals("exit")) {

            System.out.println("Goodbye");
            return true;
        }

        if (str.equals("res")) {

            main(args);
            return true;
        }

        return false;
    }

    private static boolean checkInputError(Scanner in, String[] args, String error) {

        boolean res = false;

        while (!res) {

            if (error != null) {

                System.out.println(error);
            }

            System.out.println("Для перезапуска программы введите \"res\"");
            System.out.println("Для выхода из программы введите \"exit\"");

            String str = in.nextLine();

            if (str.equals("exit")) {

                System.out.println("Goodbye");
                res = true;
            }

            if (str.equals("res")) {

                main(args);
                res = true;
            }

            error = "Ошибка ввода";
        }

        return true;
    }

    private static boolean checkOptionalFunction(String str, Scanner in, String[] args) {

        return checkGettingHelp(str, in, args) || getOptionalFunction(str, args);
    }
}
