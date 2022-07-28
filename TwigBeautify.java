import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TwigBeautify {

  private static String generateTabs(int tabs) {
    String tabString = "";
    for (int i = 0; i < tabs; i++) {
      tabString += "\t";
    }
    return tabString;
  }

  private static String removeLeadingAndTrailingTilde(String twig) {
    twig = twig.replaceAll("\\{\\{\\s~", "{{");
    twig = twig.replaceAll("~\\s\\}\\}", "}}");
    return twig;
  }

  private static String beautifyTwig(String twig) {

    twig = removeLeadingAndTrailingTilde(twig);

    Pattern pattern = Pattern.compile("((?:\\{%-?|\\{\\{-?|\\[\\[)[^\\}\\]]+(?:\\}|\\])(?:\\}|\\])?)(?=((?:\\{%-?|\\{\\{-?|\\[\\[)[^\\}\\]]+(?:\\}|\\])(?:\\}|\\])?))?");
    StringBuffer output = new StringBuffer();
    Matcher matcher = pattern.matcher(twig);
    int tabIndex = 0;
    String tag;
    String nextTag;
    String rep = "";
    boolean isDobuleLine = false;

    if (!twig.contains("\n")) {
      while (matcher.find()) {

        tag = matcher.group(1);
        nextTag = matcher.group(2);

        // set
        if (tag.contains("{% set") || tag.contains("{%- set")) {
          isDobuleLine = true;
          if (tag.contains("=")) {
            rep = String.format("%1$s\n%2$s", tag, generateTabs(tabIndex));
          } else {
            rep = String.format("%s", tag);
          }

        // endset
        } else if (tag.contains("{% endset") || tag.contains("{%- endset")) {
          isDobuleLine = true;
          rep = String.format("%1$s\n%2$s", tag, generateTabs(tabIndex));

        // if
        } else if (tag.contains("{% if") || tag.contains("{%- if")) {
          tabIndex++;
          rep = String.format("%1$s%2$s\n%3$s", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));

        // elseif and elseif
        } else if (tag.contains("{% else") || tag.contains("{%- else")) {
          tabIndex--;
          rep = String.format("%1$s\n%3$s%2$s\n%3$s\t", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));
          tabIndex++;

        // endif
        } else if (tag.contains("{% endif") || tag.contains("{%- endif")) {
          tabIndex--;
          if (nextTag != null && (nextTag.contains("{% if") || nextTag.contains("{%- if") || nextTag.contains("{{") || nextTag.contains("[["))) {
            rep = String.format("%1$s\n%3$s%2$s\n%3$s", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));
          } else {
            rep = String.format("%1$s\n%3$s%2$s", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));
          }

        // <print>
        } else if (tag.contains("{{")) {
          if (nextTag != null && (nextTag.contains("{% if") || nextTag.contains("{%- if") || nextTag.contains("{{") || nextTag.contains("[["))) {
            rep = String.format("%1$s%2$s\n%3$s", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));
          } else {
            rep = String.format("%1$s%2$s", isDobuleLine ? "\n" : "", tag);
          }

        // <custom twig>
        } else if (tag.contains("[[")) {
          if (nextTag != null && (nextTag.contains("{% endset") || nextTag.contains("{%- endset"))) {
            rep = String.format("%1$s", tag);
          } else if (nextTag != null && (nextTag.contains("{% if") || nextTag.contains("{%- if") || nextTag.contains("{{") || nextTag.contains("[["))) {
            rep = String.format("%1$s%2$s\n%3$s", isDobuleLine ? "\n" : "", tag, generateTabs(tabIndex));
          } else {
            rep = String.format("%1$s%2$s", isDobuleLine ? "\n" : "", tag);
          }

        // <other>
        } else {
          rep = String.format("%s", tag);
        }

        if (isDobuleLine && !tag.contains("{% set") && !tag.contains("{%- set") && !tag.contains("{% endset") && !tag.contains("{%- endset")) {
          isDobuleLine = false;
        }

        matcher.appendReplacement(output, rep);
      }
      matcher.appendTail(output);

      return output.toString();
    }
    return twig;
  }

  private static String makeOneLineTwig(String twig) {
    twig = twig.replaceAll("(?m)^\\s+", "");
    twig = twig.replaceAll("\\n", "");
    return twig;
  }

  private static String toggleWhitespaceControl(String twig) {
    Pattern pattern = Pattern.compile("(\\{%|\\{\\{)\\h");
    Matcher matcher = pattern.matcher(twig);

    if (matcher.find()) {
      twig = twig.replaceAll("(\\{%|\\{\\{)\\h", "$1- ");
      twig = twig.replaceAll("\\h(%\\}|\\}\\})", " -$1");
    } else {
      twig = twig.replaceAll("(\\{%|\\{\\{)-\\h", "$1 ");
      twig = twig.replaceAll("\\h-(%\\}|\\}\\})", " $1");
    }
    return twig;
  }

  private static void createGUI() {
    JFrame frame = new JFrame("Twig Beautify");

    JPanel panel = new JPanel();

    JPanel buttonPanel = new JPanel();

    JTextArea editor = new JTextArea();
    editor.setFont(new Font("Consolas", Font.PLAIN, 12));
    editor.setTabSize(2);

    JButton beautify = new JButton("Beautify");
    beautify.setBounds(10,10,10,10);
    beautify.setAlignmentX(Component.CENTER_ALIGNMENT);
    beautify.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        editor.setText(beautifyTwig(editor.getText()));
      }
    });

    JButton makeOneLine = new JButton("One line");
    makeOneLine.setBounds(10,10,10,10);
    makeOneLine.setAlignmentX(Component.CENTER_ALIGNMENT);
    makeOneLine.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        editor.setText(makeOneLineTwig(editor.getText()));
      }
    });

    JButton toggleWhitespaceControl = new JButton("Whitespace control");
    toggleWhitespaceControl.setBounds(10,10,10,10);
    toggleWhitespaceControl.setAlignmentX(Component.CENTER_ALIGNMENT);
    toggleWhitespaceControl.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        editor.setText(toggleWhitespaceControl(editor.getText()));
      }
    });

    JScrollPane scroll = new JScrollPane(editor);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    buttonPanel.add(toggleWhitespaceControl);
    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPanel.add(makeOneLine);
    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPanel.add(beautify);
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

    panel.add(scroll);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(buttonPanel);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(540,620);
    frame.setResizable(false);
    frame.setVisible(true);
  }

  public static void main(String args[]) {
    createGUI();
  }
}
