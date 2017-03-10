import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewAllNotesDialog extends JDialog {
    private JPanel contentPane;
    private JTree NotesTree;
    private JTextPane codePane;
    private JTextPane notePane;

    public ViewAllNotesDialog() {
        setContentPane(contentPane);
        setModal(true);
//        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.constructTree();
    }

    private void constructTree() {
        // TODO rename me!
        NoteManager noteManager = NoteManager.getInstance();
        Map<String, List<Note>> filePathToNotes = noteManager.getFilePathToNotes();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        Set<String> filePathSet = filePathToNotes.keySet();
        for (String filePath : filePathSet) {
            List<Note> notes = filePathToNotes.get(filePath);
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(filePath);
            root.add(parentNode);

            for (Note note : notes) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(note);
                parentNode.add(childNode);
            }
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        this.NotesTree.setModel(treeModel);
        this.NotesTree.setRootVisible(false);
        this.NotesTree.setCellRenderer(new NoteCellRenderer());

        // TODO refactor me!
        this.NotesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) NotesTree.getLastSelectedPathComponent();
                // get corresponding note
                // and display the following details on the right:
                //      associated code (highlighted code) at the top
                //      note content at the bottom

                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }

                Note currentNote = (Note) selectedNode.getUserObject();
                codePane = new JTextPane();
                Document doc = codePane.getDocument();
                
                try {
                    doc.insertString(0, currentNote.getHighlightedCode(), null);
                    System.out.println(currentNote.getHighlightedCode());
                } catch (BadLocationException ble) {
                    System.err.println("Couldn't insert initial text into text pane.");
                }

            }
        });
    }

    public JComponent getPreferredFocusedComponent() {
        return this.NotesTree;
    }

    public JComponent getPanel() {
        return this.contentPane;
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
