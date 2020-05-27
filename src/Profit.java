import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;

public class Profit {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Для справки введите \"h\" или");
        System.out.println("Введите стоимость предмета или предметов");

        String costs = getLine(scanner);

        List<BigInteger> costsList = getCostsList(costs);

        System.out.println("Если сумма продажи предмета(ов) отличается от стоимости введите \"+\"");
        System.out.println("Если нет введите \"-\"");

        String res = getLine(scanner);

        if (!checkResponse(res)) {
            checkInputError("Ошибка ввода");
        }

        if (res.equals("+")) {

            System.out.println("Укажите позицию(и)");

            String positions = getLine(scanner);

            Set<BigInteger> setPositions = getSetPositions(positions);

            checkNonexistentPositions(setPositions, costsList);

            System.out.println(getMessageForPositions(setPositions) + getStrPositions(setPositions));

            String amounts = getLine(scanner);

            List<BigInteger> amountsList = getAmountsList(amounts, setPositions, costsList);

            checkAmounts(amountsList, setPositions, costsList);

            String response = getResponse();

            if (!checkResponse(response)) {
                checkInputError("Ошибка ввода");
            }

            calculationOfProfit(costsList, amountsList, setPositions, response.equals("+"));

            checkInputError(null);

        } else {

            String response = getResponse();

            if (!checkResponse(response)) {
                checkInputError("Ошибка ввода");
            }

            calculationOfProfit(costsList, response.equals("+"));

            checkInputError(null);
        }
    }

    /**
     * Метод для получения введенной строки
     *
     * @param scanner - сканнер для считывания данных
     * @return - введенную строку
     */

    private static String getLine(Scanner scanner) {

        String line = scanner.nextLine();

        checkingAdditionalFunctions(line);

        return line;
    }

    /**
     * Метод для расчета прибыли если стоимость предмета отличается от суммы продажи
     *
     * @param costsList    - список стоимостей предметов
     * @param amountsList  - список сумм продажи
     * @param setPositions - позиции предметов
     * @param resp         - введенный ответ пользователя
     */

    private static void calculationOfProfit(List<BigInteger> costsList, List<BigInteger> amountsList, Set<BigInteger> setPositions, boolean resp) {

        BigInteger result = BigInteger.valueOf(0);
        BigInteger bonus = BigInteger.valueOf(0);

        for (int i = 0; i < costsList.size(); i++) {

            BigInteger cost = costsList.get(i);

            for (BigInteger pos : setPositions) {

                if (BigInteger.valueOf(i).equals(pos)) {

                    BigInteger amount = amountsList.get(i);

                    BigInteger res = amount.subtract(cost);

                    bonus = res.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(20));
                }
            }

            BigInteger sum = cost.add(bonus).divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(70));
            result = result.add(sum);
        }

        result = getResult(resp, result);

        System.out.println("Ваша прибыль = " + result);
    }

    /**
     * Метод для расчета прибыли если стоимость предмета не отличается от суммы продажи
     *
     * @param costsList - список стоимостей предметов
     * @param resp      - введенный ответ пользователя
     */

    private static void calculationOfProfit(List<BigInteger> costsList, boolean resp) {

        BigInteger result = BigInteger.valueOf(0);

        for (BigInteger cost : costsList) {

            BigInteger sum = cost.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(70));
            result = result.add(sum);
        }

        result = getResult(resp, result);

        System.out.println("Ваша прибыль = " + result);
    }

    /**
     * Метод для получения результата подсчета прибыли
     *
     * @param resp   - ответ пользователя
     * @param result - результат расчета
     * @return - результат расчета прибыли
     */

    private static BigInteger getResult(boolean resp, BigInteger result) {

        if (resp) {
            result = result.add(result.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(30)));
        }

        return result;
    }

    /**
     * Метод для получения списка сумм продажи предметов
     *
     * @param amounts      - суммы указанные пользователем
     * @param setPositions - список позиций
     * @return - список сумм продажи предметов
     */

    private static List<BigInteger> getAmountsList(String amounts, Set<BigInteger> setPositions, List<BigInteger> costsList) {

        if (!checkByPattern(amounts)) {
            checkInputError("Строка не число");
        }

        List<String> listStrNum = getListString(amounts);

        checkQuantitiesAmounts(listStrNum, setPositions);

        List<BigInteger> amountsList = new ArrayList<>(costsList);
        List<BigInteger> positionsList = new ArrayList<>(setPositions);

        for (int i = 0; i < amountsList.size(); i++) {

            amountsList.set(positionsList.get(i).intValue(), new BigInteger(listStrNum.get(i)));

            if (i == positionsList.size() - 1) {
                break;
            }
        }

        return amountsList;
    }

    /**
     * Метод для проверки несуществующих позиций
     *
     * @param setPositions - введенные позиции
     * @param costsList    - список стоимостей предметов
     */

    private static void checkNonexistentPositions(Set<BigInteger> setPositions, List<BigInteger> costsList) {

        Set<BigInteger> setNonexistentPositions = getNonexistentPositions(setPositions, costsList);

        if (setNonexistentPositions.size() == 0) {
            return;
        }

        List<BigInteger> listNonexistentPositions = new ArrayList<>(setNonexistentPositions);

        StringBuilder strNonexistentPositions = new StringBuilder();

        String delimiter;

        for (BigInteger nonexistentPosition : listNonexistentPositions) {

            delimiter = getDelimiter(listNonexistentPositions, nonexistentPosition);

            strNonexistentPositions.append(nonexistentPosition).append(delimiter);
        }

        checkInputError(getMessageForNonexistentPositions(listNonexistentPositions, strNonexistentPositions));
    }

    /**
     * Метод для получения сообщения для несуществующих позиций
     *
     * @param listNonexistentPositions - список несуществующих позиций
     * @param strNonexistentPositions - строка несуществующих позиций
     * @return - сообщение для несуществующих позиций
     */

    private static String getMessageForNonexistentPositions(List<BigInteger> listNonexistentPositions, StringBuilder strNonexistentPositions) {

        String message;

        if (listNonexistentPositions.size() > 1) {
            message = "Позиции " + strNonexistentPositions.toString() + " не найдены";
        } else {
            message = "Позиция " + strNonexistentPositions.toString() + " не найдена";
        }

        return message;
    }

    /**
     * Метод для получения разделителя строки
     *
     * @param positions - позиции
     * @param position  - позиция
     * @return - разделитель
     */

    private static String getDelimiter(List<BigInteger> positions, BigInteger position) {

        if (positions.size() == 1 || position.equals(positions.get(positions.size() - 1))) {
            return "";
        }

        return ", ";
    }

    /**
     * Метод для получения несуществующих позиций
     *
     * @param setPositions - введенные позиции
     * @param costsList    - список стоимостей предметов
     * @return - несуществующие позиции
     */

    private static Set<BigInteger> getNonexistentPositions(Set<BigInteger> setPositions, List<BigInteger> costsList) {

        checkQuantitiesPositions(setPositions, costsList);

        Set<BigInteger> setNonexistentPositions = new HashSet<>();

        for (BigInteger position : setPositions) {

            boolean check = false;

            for (int i = 0; i < costsList.size(); i++) {

                if (position.equals(BigInteger.valueOf(i))) {
                    check = true;
                    break;
                }
            }

            if (!check) {
                setNonexistentPositions.add(position);
            }
        }

        return setNonexistentPositions;
    }

    /**
     * Метод для проверки количества введенных позиций
     *
     * @param setPositions - введенные позиции
     * @param costsList    - список стоимостей предметов
     */

    private static void checkQuantitiesPositions(Set<BigInteger> setPositions, List<BigInteger> costsList) {
        if (setPositions.size() > costsList.size()) {
            checkInputError("Количество позиций превышает количество предметов");
        }
    }

    /**
     * Метод для получения строки с позициями
     *
     * @param setPositions - введенные позиции
     * @return - строку с позициями
     */

    private static String getStrPositions(Set<BigInteger> setPositions) {

        List<BigInteger> listPositions = new ArrayList<>(setPositions);

        StringBuilder strPositions = new StringBuilder();

        String delimiter;

        for (BigInteger position : setPositions) {

            delimiter = getDelimiter(listPositions, position);

            strPositions.append(position).append(delimiter);
        }

        return strPositions.toString();
    }

    /**
     * Метод для проверки сумм из указанных позиций
     *
     * @param amountsList  - список сумм указанных пользователем
     * @param setPositions - указанные позиции
     * @param costsList    - список стоимостей предметов
     */

    private static void checkAmounts(List<BigInteger> amountsList, Set<BigInteger> setPositions, List<BigInteger> costsList) {

        for (int i = 0; i < setPositions.size(); i++) {

            BigInteger amount = amountsList.get(i);
            BigInteger cost = costsList.get(i);

            if (amount.compareTo(cost) < 0) {
                checkInputError("Сумма продажи не может быть меньше стоимости предмета");
            }
        }
    }

    /**
     * Метод для проверки количества введенных сумм
     *
     * @param listStrNum   - список сумм
     * @param setPositions - указанные позиции
     */

    private static void checkQuantitiesAmounts(List<String> listStrNum, Set<BigInteger> setPositions) {

        if (listStrNum.size() > setPositions.size()) {
            checkInputError("Количество сумм превышает количество позиций");
        }

        if (listStrNum.size() < setPositions.size()) {
            checkInputError("Не указаны все цены для позиций");
        }
    }

    /**
     * Метод для получения списка стоимостей предметов
     *
     * @param str - введенная строка
     * @return - список стоимостей предметов
     */

    private static List<BigInteger> getCostsList(String str) {

        if (!checkByPattern(str)) {
            checkInputError("Строка не число");
        }

        List<String> listStrNum = getListString(str);
        List<BigInteger> costsList = new ArrayList<>();

        for (String numStr : listStrNum) {

            BigInteger number = new BigInteger(numStr);

            if (number.equals(BigInteger.valueOf(0))) {
                checkInputError("Число должно быть больше нуля");
            }

            costsList.add(number);
        }

        for (int i = 0; i < costsList.size(); i++) {
            System.out.println("Позиция " + i + " : " + costsList.get(i));
        }

        return costsList;
    }

    /**
     * Метод для получения позиций из строки
     *
     * @param str - введенная строка
     * @return - список позиций из строки
     */

    private static Set<BigInteger> getSetPositions(String str) {

        if (!checkByPattern(str)) {
            checkInputError("Строка не число");
        }

        List<String> listNumStr = getListString(str);
        Set<BigInteger> setBigInteger = new HashSet<>();

        for (String numStr : listNumStr) {
            setBigInteger.add(new BigInteger(numStr));
        }

        return setBigInteger;
    }

    /**
     * Метод для получения сообщения для позиций
     *
     * @param setPositions - позиции предметов
     * @return - сообщение для позиций
     */

    private static String getMessageForPositions(Set<BigInteger> setPositions) {

        String message;

        if (setPositions.size() > 1) {
            message = "Укажите суммы для позиций ";
        } else {
            message = "Укажите сумму для позиции ";
        }

        return message;
    }

    /**
     * Метод для получения чисел из введенной строки
     *
     * @param str - строка
     * @return - список строк с числами
     */

    private static List<String> getListString(String str) {

        str = str.replaceAll("[^0-9]+", " ");

        return Arrays.asList(str.trim().split(" "));
    }

    /**
     * Метод для проверки чисел в строке
     *
     * @param str - строка
     * @return - результат проверки
     */

    private static boolean checkByPattern(String str) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);

        return matcher.find();
    }

    /**
     * Метод для получения ответа от пользователя
     *
     * @return - ответ от пользователя
     */

    private static String getResponse() {

        System.out.println("Если считать с комфортом введите " + "\"+\"");
        System.out.println("Если считать без комфорта введите " + "\"-\"");

        return new Scanner(System.in).nextLine();
    }

    /**
     * Метод для проверки ответа от пользователя
     *
     * @param response - введенный ответ от пользователя
     * @return - результат проверки
     */

    private static boolean checkResponse(String response) {

        return response.equals("+") || response.equals("-");
    }

    /**
     * Метод для проверки ввода помощи
     *
     * @param str - введенная строка
     * @return - результат проверки
     */

    private static boolean checkInputHelp(String str) {

        Scanner scanner = new Scanner(System.in);

        if (str.equals("h")) {

            getHelp();

            String resp = scanner.nextLine();

            if (!checkingInputString(resp)) {
                checkInputError("Ошибка ввода");
            }
        }

        return false;
    }

    /**
     * Метод для получение помощи
     */

    private static void getHelp() {
        System.out.println("Программа была создана для подсчёта прибыли с аукциона в игре black desert");
        System.out.println("В программу можно ввести только целочисленные значения не меньше нуля");
        System.out.println("Для перезапуска программы введите \"res\"");
        System.out.println("Для выхода из программы введите \"exit\"");
    }

    /**
     * Метод для проверки дополнительных функций
     *
     * @param str - введенная строка
     * @return - результат проверки
     */

    private static boolean checkingInputString(String str) {

        if (str.equals("res")) {

            String jarName = "CalculationOfProfit.jar";
            String batName = "RunCalculation.bat";

            try {

                if (!existsFile(jarName) || !existsFile(batName)) {
                    System.exit(0);
                }

                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", new File(batName).getPath()});
                System.exit(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (str.equals("exit")) {
            System.out.println("Goodbye");
            System.exit(0);
        }

        return false;
    }

    /**
     * Метод для проверки существования файла
     *
     * @param fileName - имя файла
     * @return - результат проверки
     */

    private static boolean existsFile(String fileName) {

        String path = new File(fileName).getAbsolutePath();
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("in directory " + new File("").getAbsolutePath() + File.separator + " file " + fileName + " not found");
            return false;
        }

        return true;
    }

    /**
     * Метод для проверки ошибки ввода
     *
     * @param error - ошибка
     */

    private static void checkInputError(String error) {

        Scanner scanner = new Scanner(System.in);

        boolean res = false;

        while (!res) {

            if (error != null) {
                System.out.println(error);
            }

            System.out.println("Для перезапуска программы введите \"res\"");
            System.out.println("Для выхода из программы введите \"exit\"");

            String str = scanner.nextLine();

            if (!checkingInputString(str)) {
                error = "Ошибка ввода";
            } else {
                res = true;
            }
        }
    }

    /**
     * Метод для проверки дополнительных функций
     *
     * @param str - введенная строка
     */

    private static void checkingAdditionalFunctions(String str) {
        checkInputHelp(str);
        checkingInputString(str);
    }
}
