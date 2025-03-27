/*あ
 * 課題番号：b05-02
 * 概要：文字列分配
 * 作成年月日：24/10/25
 * 学籍番号：0312024011
 * 氏名：五十嵐湖香
 */

#include <stdio.h>
#include <string.h>

void str_dist(char *str, char *x, char *y) {
    int len = strlen(str);
    for (int i = 0; i < len; i++) {
        if (i % 2 == 0) {
            x[i / 2] = str[i];
        } else {
            y[i / 2] = str[i];
        }
    }
    x[len / 2 + (len % 2)] = '\0';
    y[len / 2] = '\0';
}

int main(void) {
    char str[128];
    char a[64], b[64];

    printf("文字列：");
    scanf("%127s", str);

    str_dist(str, a, b);

    printf("分配文字列１＝%s\n", a);
    printf("分配文字列２＝%s\n", b);

    return 0;
}
