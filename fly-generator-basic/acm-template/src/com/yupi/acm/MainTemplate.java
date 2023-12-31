import java.util.Scanner;

/**
* ACM 输入模板（多数之和）
* @author fly
*/
public class MainTemplate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入元素个数�?");

            // 读取输入元素个数
            int n = scanner.nextInt();
            // 读取数组
            System.out.println("输入元素�?");
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = scanner.nextInt();
            }

            // 处理问题逻辑，根据需要进行输�?
            // 示例：计算数组元素的�?
            int sum = 0;
            for (int num : arr) {
                sum += num;
            }

            System.out.println("hhhh" + sum);
            scanner.close();
    }
}
