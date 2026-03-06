package com.aiegoo.comicrental.tui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.aiegoo.comicrental.App;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

/**
 * A small Lanterna‑based user interface that mirrors the CLI commands.
 *
 * This class is intentionally minimal; as the TUI evolves it can be split
 * into multiple screens, dialogs and helper classes.  For now the menu lists
 * basic operations and displays command output in a dialog box.  The
 * underlying {@link App} instance is invoked with its existing command parser
 * so that business logic stays in one place.
 *
 * To compile/run you will need the Lanterna jar on the classpath; set
 * environment variable TUI_JAR to point at it and use `make tui`.
 */
public class Tui {
    private final App app = new App();

    public static void main(String[] args) throws Exception {
        // register DB shutdown hook and load driver
        DBConnectionUtil.registerShutdownHook();
        // TUI will manage its own output, so turn off noisy connection logging
        DBConnectionUtil.setVerbose(false);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        try (Screen screen = factory.createScreen()) {
            screen.startScreen();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("Comic Rental Dashboard");

            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            panel.addComponent(new Label("Use arrows or numbers to navigate.  Press Enter to select."));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

            ActionListBox listBox = new ActionListBox();
            Tui tui = new Tui();
            listBox.addItem("1) List comics", () -> tui.showOutput(gui, "comic-list"));
            listBox.addItem("2) List members", () -> tui.showOutput(gui, "member-list"));
            listBox.addItem("3) List rentals", () -> tui.showOutput(gui, "rental-list"));
            listBox.addItem("4) Add comic", () -> tui.showForm(gui, "comic-add", new String[] {"Title","Volume","Author"}));
            listBox.addItem("5) Add member", () -> tui.showForm(gui, "member-add", new String[] {"Name","Phone"}));
            listBox.addItem("6) Rent comic", () -> tui.showForm(gui, "rent", new String[] {"Comic ID","Member ID"}));
            listBox.addItem("7) Return comic", () -> tui.showForm(gui, "return", new String[] {"Rental ID"}));
            listBox.addItem("0) Exit", window::close);
            panel.addComponent(listBox);

            window.setComponent(panel);
            gui.addWindowAndWait(window);
        }
    }

    private void showOutput(WindowBasedTextGUI gui, String command) {
        try {
            String result = executeCliCommand(command);
            // display result in a scrollable, read-only text box instead of a simple message dialog
            BasicWindow win = new BasicWindow("Result");
            Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
            TextBox outputBox = new TextBox(new TerminalSize(50, 20), TextBox.Style.MULTI_LINE);
            outputBox.setReadOnly(true);
            outputBox.setText(result == null ? "" : result);
            panel.addComponent(outputBox.withBorder(Borders.singleLine("Output")));
            panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
            panel.addComponent(new Button("OK", win::close));
            win.setComponent(panel);
            gui.addWindowAndWait(win);
        } catch (Exception e) {
            BasicWindow errWin = new BasicWindow("Error");
            Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
            TextBox outputBox = new TextBox(new TerminalSize(50, 6), TextBox.Style.MULTI_LINE);
            outputBox.setReadOnly(true);
            outputBox.setText(e.getMessage());
            panel.addComponent(outputBox.withBorder(Borders.singleLine("Error")));
            panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
            panel.addComponent(new Button("OK", errWin::close));
            errWin.setComponent(panel);
            gui.addWindowAndWait(errWin);
        }
    }

    private void showForm(WindowBasedTextGUI gui, String baseCommand, String[] prompts) {
        BasicWindow formWin = new BasicWindow("Input");
        Panel form = new Panel(new LinearLayout(Direction.VERTICAL));
        TextBox[] inputs = new TextBox[prompts.length];
        for (int i = 0; i < prompts.length; i++) {
            form.addComponent(new Label(prompts[i] + ": "));
            inputs[i] = new TextBox();
            form.addComponent(inputs[i]);
        }
        form.addComponent(new EmptySpace(new TerminalSize(0,1)));
        form.addComponent(new com.googlecode.lanterna.gui2.Button("OK", () -> {
            StringBuilder cmd = new StringBuilder(baseCommand);
            for (TextBox t : inputs) {
                cmd.append(" ").append(t.getText().trim());
            }
            formWin.close();
            showOutput(gui, cmd.toString());
        }));
        formWin.setComponent(form);
        gui.addWindowAndWait(formWin);
    }

    private String executeCliCommand(String cmd) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newStream = new PrintStream(baos);
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setOut(newStream);
        System.setErr(newStream);
        try {
            app.handle(cmd, new Scanner(System.in));
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
        return baos.toString();
    }
}
