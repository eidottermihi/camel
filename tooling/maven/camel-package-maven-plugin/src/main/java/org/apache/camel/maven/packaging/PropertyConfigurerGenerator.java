/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven.packaging;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.apache.camel.tooling.model.BaseOptionModel;

public final class PropertyConfigurerGenerator {

    private PropertyConfigurerGenerator() {
    }

    public static void generateExtendConfigurer(String pn, String cn, String pfqn, String psn, Writer w) throws IOException {
        w.write("/* " + AbstractGeneratorMojo.GENERATED_MSG + " */\n");
        w.write("package " + pn + ";\n");
        w.write("\n");
        w.write("import " + pfqn + ";\n");
        w.write("\n");
        w.write("/**\n");
        w.write(" * " + AbstractGeneratorMojo.GENERATED_MSG + "\n");
        w.write(" */\n");
        w.write("public class " + cn + " extends " + psn + " {\n");
        w.write("\n");
        w.write("}\n");
        w.write("\n");
    }

    public static void generatePropertyConfigurer(String pn, String cn, String en, Collection<? extends BaseOptionModel> options, Writer w) throws IOException {
        w.write("/* " + AbstractGeneratorMojo.GENERATED_MSG + " */\n");
        w.write("package " + pn + ";\n");
        w.write("\n");
        w.write("import org.apache.camel.CamelContext;\n");
        w.write("import org.apache.camel.spi.GeneratedPropertyConfigurer;\n");
        w.write("import org.apache.camel.support.component.PropertyConfigurerSupport;\n");
        w.write("\n");
        w.write("/**\n");
        w.write(" * " + AbstractGeneratorMojo.GENERATED_MSG + "\n");
        w.write(" */\n");
        w.write("@SuppressWarnings(\"unchecked\")\n");
        w.write("public class " + cn + " extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer {\n");
        w.write("\n");
        w.write("    @Override\n");
        w.write("    public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {\n");
        w.write("        if (ignoreCase) {\n");
        w.write("            return doConfigureIgnoreCase(camelContext, target, name, value);\n");
        w.write("        } else {\n");
        w.write("            return doConfigure(camelContext, target, name, value);\n");
        w.write("        }\n");
        w.write("    }\n");
        w.write("\n");
        w.write("    private static boolean doConfigure(CamelContext camelContext, Object target, String name, Object value) {\n");
        w.write("        switch (name) {\n");
        for (BaseOptionModel option : options) {
            String getOrSet = option.getName();
            getOrSet = Character.toUpperCase(getOrSet.charAt(0)) + getOrSet.substring(1);
            String setterLambda = setterLambda(en, getOrSet, option.getJavaType(), option.getConfigurationField());
            w.write(String.format("        case \"%s\": %s; return true;\n", option.getName(), setterLambda));
        }
        w.write("            default: return false;\n");
        w.write("        }\n");
        w.write("    }\n");
        w.write("\n");
        w.write("    private static boolean doConfigureIgnoreCase(CamelContext camelContext, Object target, String name, Object value) {\n");
        w.write("        switch (name.toLowerCase()) {\n");
        for (BaseOptionModel option : options) {
            String getOrSet = option.getName();
            getOrSet = Character.toUpperCase(getOrSet.charAt(0)) + getOrSet.substring(1);
            String setterLambda = setterLambda(en, getOrSet, option.getJavaType(), option.getConfigurationField());
            w.write(String.format("        case \"%s\": %s; return true;\n", option.getName().toLowerCase(), setterLambda));
        }
        w.write("            default: return false;\n");
        w.write("        }\n");
        w.write("    }\n");
        w.write("\n");
        w.write("}\n");
        w.write("\n");
    }

    private static String setterLambda(String en, String getOrSet, String type, String configurationField) {
        // type may contain generics so remove those
        if (type.indexOf('<') != -1) {
            type = type.substring(0, type.indexOf('<'));
        }
        type = type.replace('$', '.');
        if (configurationField != null) {
            getOrSet = "get" + Character.toUpperCase(configurationField.charAt(0)) + configurationField.substring(1) + "().set" + getOrSet;
        } else {
            getOrSet = "set" + getOrSet;
        }

        // ((LogComponent) target).setGroupSize(property(camelContext,
        // java.lang.Integer.class, value))
        return String.format("((%s) target).%s(property(camelContext, %s.class, value))", en, getOrSet, type);
    }

}
