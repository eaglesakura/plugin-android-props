package com.eaglesakura.gradle.android.property

import com.eaglesakura.android.property.model.PropertySource
import com.eaglesakura.tool.generator.CodeWriter

/**
 * JSONのプロパティシートベースで書き出しを行う
 */
public class PropClassGenerator2 {
    def classPackageName = "com.example";
    def className = "SampleSettingClass";
    def superClass = "com.eaglesakura.android.property.internal.GeneratedProperties"
    File outDirectory = null;

    /**
     * 頭の１文字目を大文字にする
     */
    static String toCamelCase(String base) {
        // スネークケースをキャメルケースに変換する
        CNV_SNAKE:
        {
            def split = base.split("_");
            if (split.length > 1) {
                String temp = "";
                for (def item : split) {
                    temp += "${item.substring(0, 1).toUpperCase()}${item.substring(1)}";
                }

                base = temp;
            }
        }

        return "${base.substring(0, 1).toUpperCase()}${base.substring(1)}";
    }
    /**
     * 保持しているプロパティ一覧
     */
    private List<Property> properties = new ArrayList<Property>();

    private static boolean asBoolean(String value) {
        value = value.toLowerCase();
        switch (value) {
            case "true":
                return true;
            case "false":
                return false;
            case "0":
                return false;
            default:
                return true;
        }
    }

    /**
     * プロパティシートをJSONモデルから組み立てる
     * @param group
     */
    public void addProperties(PropertySource.Group group) {
        className = group.name;
        for (def prop : group.getProperties()) {
            switch (prop.type) {
                case "String":
                    stringProperty(prop.name, prop.value);
                    break;
                case "float":
                    floatProperty(prop.name, prop.value as float);
                    break;
                case "double":
                    doubleProperty(prop.name, prop.value as double);
                    break;
                case "int":
                    intProperty(prop.name, prop.value as int);
                    break;
                case "long":
                    longProperty(prop.name, prop.value as long);
                    break;
                case "boolean":
                    booleanProperty(prop.name, asBoolean(prop.value));
                    break;
                case "byte[]":
                    byteArrayProperty(prop.name);
                    break;
                case "Bitmap":
                    bitmapProperty(prop.name);
                    break;
                case "Date":
                    dateProperty(prop.name);
                    break;
            }
        }
    }

    public void byteArrayProperty(String propName) {
        properties.add(new Property("${className}.${propName}", propName, "") {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(byte[] set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public byte[] get${toCamelCase(name)}(){ return getByteArrayProperty(\"${key}\"); }";
            }
        })
    }

    public void floatProperty(String propName, float propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, "" + propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(float set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public float get${toCamelCase(name)}(){ return getFloatProperty(\"${key}\"); }";
            }
        })
    }

    public void doubleProperty(String propName, double propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, "" + propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(double set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public double get${toCamelCase(name)}(){ return getDoubleProperty(\"${key}\"); }";
            }
        })
    }

    public void booleanProperty(String propName, boolean propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, propDefaultValue ? "1" : "0") {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(boolean set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public boolean is${toCamelCase(name)}(){ return getBooleanProperty(\"${key}\"); }";
            }
        })
    }

    public void intProperty(String propName, int propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, "" + propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(int set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public int get${toCamelCase(name)}(){ return getIntProperty(\"${key}\"); }";
            }
        })
    }

    public void longProperty(String propName, long propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, "" + propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(long set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public long get${toCamelCase(name)}(){ return getLongProperty(\"${key}\"); }";
            }
        })
    }

    public void dateProperty(String propName) {
        properties.add(new Property("${className}.${propName}", propName, "0") {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(java.util.Date set){ setProperty(\"${key}\", set != null ? set.getTime() : 0); }";
            }

            @Override
            String generateGetter() {
                return "public java.util.Date get${toCamelCase(name)}(){ return getDateProperty(\"${key}\"); }";
            }
        })
    }

    public void stringProperty(String propName, String propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(String set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public String get${toCamelCase(name)}(){ return getStringProperty(\"${key}\"); }";
            }
        })
    }

    /**
     * enum型のPropertiesを生成する
     * @param propName
     * @param enumFullName
     * @param propDefaultValue
     */
    public void enumProperty(String propName, final String enumFullName, String propDefaultValue) {
        properties.add(new Property("${className}.${propName}", propName, propDefaultValue) {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(${enumFullName} set){ setProperty(\"${key}\", set != null ? set.name() : \"\"); }";
            }

            @Override
            String generateGetter() {
                return "public ${enumFullName} get${toCamelCase(name)}(){ try{ return ${enumFullName}.valueOf(getStringProperty(\"${key}\")); }catch(Exception e){ return null; } }";
            }
        })
    }

    /**
     * JSON型のPropertiesを生成する
     * @param name プロパティ名
     * @param pojoFullName JSONの
     */
    public void jsonProperty(String propName, final String pojoFullName) {
        properties.add(new Property("${className}.${propName}", propName, "{}") {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(${pojoFullName} set){ setProperty(\"${key}\", com.eaglesakura.json.JSON.encodeOrNull(set)); }";
            }

            @Override
            String generateGetter() {
                return "public ${pojoFullName} get${toCamelCase(name)}(){ return getJsonProperty(\"${key}\", ${pojoFullName}.class); }";
            }
        })
    }

    /**
     * Bitmap用Propertyを追加する
     * @param propName
     */
    public void bitmapProperty(String propName) {
        properties.add(new Property("${className}.${propName}", propName, "") {
            @Override
            String generateSetter() {
                return "public void set${toCamelCase(name)}(android.graphics.Bitmap set){ setProperty(\"${key}\", set); }";
            }

            @Override
            String generateGetter() {
                return "public android.graphics.Bitmap get${toCamelCase(name)}(){ return getBitmapProperty(\"${key}\"); }";
            }
        })
    }

    public void build() {

        File srcRootDirectory = outDirectory;

        FILE_CHECK:
        {
            // 規定の経路を生成する
            String[] dirs = classPackageName.split("\\.");
            for (String s : dirs) {
                srcRootDirectory = new File(srcRootDirectory, s);
            }
            srcRootDirectory.mkdirs();

            // ファイル名指定
            srcRootDirectory = new File(srcRootDirectory, "${className}.java");
        }

        CodeWriter writer = new CodeWriter(srcRootDirectory);

        // packagename
        writer.writeLine("package ${classPackageName};").newLine();

        // import
//        writer.writeLine("import android.content.Context;")
//        writer.writeLine("import java.io.File;");
        writer.newLine();

        // class name
        writer.writeLine("public class ${className} extends ${superClass} {").pushIndent(true);

        // プロパティIDを出力
        PROP_ID:
        {
            writer.newLine();
            for (def prop : properties) {
                writer.writeLine("public static final String ID_${prop.name.toUpperCase()} = \"${prop.key}\";");
            }
            writer.newLine();
        }

        // コンストラクタと初期化
        INIT:
        {
            writer.writeLine("public ${className}(){ }");
        }

        // アクセサメソッドを生成する
        Accr:
        {
            for (Property prop : properties) {
                writer.writeLine(prop.generateSetter());
                writer.writeLine(prop.generateGetter());
            }
        }
        writer.popIndent(true).writeLine("}");

        // 生成完了
        writer.commit();
    }

    /**
     * 設定項目を指定する
     */
    static abstract class Property {
        final String defaultValue;

        final String key;

        final String name;

        Property(String key, String name, String defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        /**
         * setter用コードを生成する
         */
        abstract String generateSetter();

        /**
         * getter用コードを生成する
         */
        abstract String generateGetter();
    }
}