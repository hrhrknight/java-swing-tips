// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import javax.swing.*;

public class MainPanel extends JPanel {
  protected final JTree tree = new JTree();
  protected final JTextField field = new JTextField("", 10);
  protected final JButton button = new JButton("Find Next(dummy)");
  protected final JButton showHideButton = new JButton();
  protected Timer animator;
  protected boolean isHidden = true;
  protected final JPanel controls = new JPanel(new BorderLayout(5, 5) {
    protected int controlsHeight;
    protected int controlsPreferredHeight;
    @Override public Dimension preferredLayoutSize(Container target) {
      // synchronized (target.getTreeLock()) {
      Dimension ps = super.preferredLayoutSize(target);
      controlsPreferredHeight = ps.height;
      if (Objects.nonNull(animator)) {
        if (isHidden) {
          if (controls.getHeight() < controlsPreferredHeight) {
            controlsHeight += 5;
          }
        } else {
          if (controls.getHeight() > 0) {
            controlsHeight -= 5;
          }
        }
        if (controlsHeight <= 0) {
          controlsHeight = 0;
          animator.stop();
        } else if (controlsHeight >= controlsPreferredHeight) {
          controlsHeight = controlsPreferredHeight;
          animator.stop();
        }
      }
      ps.height = controlsHeight;
      return ps;
    }
  });

  public MainPanel() {
    super(new BorderLayout());
    add(makePanel());
    setPreferredSize(new Dimension(320, 240));
  }

  private JPanel makePanel() {
    button.setFocusable(false);
    controls.setBorder(BorderFactory.createTitledBorder("Search down"));
    controls.add(new JLabel("Find what:"), BorderLayout.WEST);
    controls.add(field);
    controls.add(button, BorderLayout.EAST);
    Action act = new AbstractAction("Show/Hide Search Box") {
      @Override public void actionPerformed(ActionEvent ev) {
        if (Objects.nonNull(animator) && animator.isRunning()) {
          return;
        }
        isHidden = controls.getHeight() == 0;
        animator = new Timer(5, e -> controls.revalidate());
        animator.start();
      }
    };
    showHideButton.setAction(act);
    showHideButton.setFocusable(false);
    JPanel p = new JPanel(new BorderLayout());

    int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    // Java 10: int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    InputMap im = p.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, modifiers), "open-searchbox");
    p.getActionMap().put("open-searchbox", act);
    p.add(controls, BorderLayout.NORTH);
    p.add(new JScrollPane(tree));
    p.add(showHideButton, BorderLayout.SOUTH);
    return p;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    // frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
