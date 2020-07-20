package cn.geekelmon.app.api;


import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            String[] strings = str.split(" ");
            HashSet<Str> set = new HashSet<>();
            for (String string : strings) {
                set.add(new Str(string));
            }

            ArrayList<Str> arrayList = new ArrayList<Str>(set);
            arrayList.sort(new Comparator<Str>() {
                @Override
                public int compare(Str str1, Str str2) {
                    char[] chars1 = str1.sss.toUpperCase().toCharArray();
                    char[] chars2 = str2.sss.toUpperCase().toCharArray();
                    int index = chars1.length < chars2.length ? chars1.length : chars2.length;
                    for (int i = 0; i < index; i++) {
                        if (chars1[i] == chars2[i]) {
                            continue;
                        }
                        return chars1[i] - chars2[i];
                    }
                    return 1;
                }
            });
            for (int i = 0; i < arrayList.size(); i++) {
                if (i == arrayList.size() - 1) {
                    System.out.print(arrayList.get(i));
                } else {
                    System.out.print(arrayList.get(i) + " ");
                }
            }
        }
    }

    public static class Str {
        String sss;

        public Str(String sss) {
            if (sss == null) {
                this.sss = "";
            }
            this.sss = sss;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Str str = (Str) o;
            return sss != null ? sss.toUpperCase().equals(str.sss.toUpperCase()) : str.sss == null;
        }

        @Override
        public int hashCode() {
            return sss != null ? sss.toUpperCase().hashCode() : 0;
        }

        @Override
        public String toString() {
            return sss;
        }
    }
}
