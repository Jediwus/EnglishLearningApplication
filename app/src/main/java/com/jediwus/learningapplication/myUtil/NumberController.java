package com.jediwus.learningapplication.myUtil;

import java.util.Random;

public class NumberController {
    /**
     * 得到区间里的一个随机数，两个端点都能取到
     * 以下函数可确保 生成在[min,max]之间的随机整数
     *
     * @param min int
     * @param max int
     * @return the random number
     */
    public static int getRandomNumber(int min, int max) {
        if (min == 0) {
            Random random = new Random();
            return random.nextInt(max + 1);
        } else if (min != max) {
            Random random = new Random();
            return random.nextInt(max) % (max - min + 1) + min;
        } else {
            return max;
        }
    }

    /**
     * 得到区间里的 n 个随机数，参数 n 必须大于 0
     *
     * @param min int
     * @param max int
     * @param n   int
     * @return the int [ ]
     */
    public static int[] getRandomNumberArray(int min, int max, int n) {
        // 判错语句
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n]; // 用于存放结果的数组
        int count = 0;
        while (count < n) {
            int num = getRandomNumber(min, max);
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if (num == result[j]) {
                    flag = false; // 保证数组内无重复值
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 得到区间里的 n 个随机数，且排除特定数字 except，参数 n 必须大于 0
     *
     * @param min    the min
     * @param max    the max
     * @param n      the n
     * @param except the except
     * @return the int [ ]
     */
    public static int[] getRandomExceptList(int min, int max, int n, int except) {
        //判断是否已经达到索要输出随机数的个数
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        //用于存放结果的数组
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = getRandomNumber(min, max);
            while (num == except) {
                // 排除和 except 相等的数字
                num = getRandomNumber(min, max);
            }
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

}
