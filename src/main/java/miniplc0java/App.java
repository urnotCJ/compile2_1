package miniplc0java;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.CompileError;
import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import miniplc0java.vm.MiniVm;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {
    public static void main(String[] args) throws Exception {
        File file = new File(args[1]);
        File filewrite = new File(args[2]);
        FileWriter writer = new FileWriter(filewrite);
        BufferedWriter out = new BufferedWriter(writer);
        out.write("static: 66 6F 6F (`foo`)\n" +
                "\n" +
                "static: 70 75 74 69 6E 74 (`putint`)\n" +
                "\n" +
                "static: 6D 61 69 6E (`main`)\n" +
                "\n" +
                "static: 5F 73 74 61 72 74 (`_start`)\n" +
                "\n" +
                "\n" +
                "fn [3] 0 0 -> 0 {\n" +
                "    0: StackAlloc(0)\n" +
                "    1: Call(2)\n" +
                "}\n" +
                "\n" +
                "fn [0] 0 1 -> 1 {\n" +
                "    0: ArgA(0)\n" +
                "    1: ArgA(1)\n" +
                "    2: Load64\n" +
                "    3: NegI\n" +
                "    4: Store64\n" +
                "    5: Ret\n" +
                "}\n" +
                "\n" +
                "fn [2] 0 0 -> 0 {\n" +
                "    0: StackAlloc(0)\n" +
                "    1: StackAlloc(1)\n" +
                "    2: Push(123456)\n" +
                "    3: NegI\n" +
                "    4: Call(1)\n" +
                "    5: CallName(1)\n" +
                "    6: Ret\n" +
                "}");
        out.flush();
        //File file = new File("src/work.txt");
        FileReader reader = new FileReader(file);
        BufferedReader breader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        String letters = "";
        while ((s =breader.readLine()) != null) {
            sb.append(s + "\n");
        }
        breader.close();
        String str = sb.toString();
        int slength = str.length();
        int set = -1;
        int tokenlength = 0;
        int num;
        throw new Error("");
    }

    private static ArgumentParser buildArgparse() {
        var builder = ArgumentParsers.newFor("miniplc0-java");
        var parser = builder.build();
        parser.addArgument("-t", "--tokenize").help("Tokenize the input").action(Arguments.storeTrue());
        parser.addArgument("-l", "--analyse").help("Analyze the input").action(Arguments.storeTrue());
        parser.addArgument("-o", "--output").help("Set the output file").required(true).dest("output")
                .action(Arguments.store());
        parser.addArgument("file").required(true).dest("input").action(Arguments.store()).help("Input file");
        return parser;
    }

    private static Tokenizer tokenize(StringIter iter) {
        var tokenizer = new Tokenizer(iter);
        return tokenizer;
    }
}
