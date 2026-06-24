import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class TodoGUI {
    JFrame frame;
    JTextField taskField, searchField;
    JComboBox<String> priorityBox;
    JTable table;
    DefaultTableModel model;
    JLabel totalLabel, completedLabel, pendingLabel, statusLabel;

    public TodoGUI() {
        frame = new JFrame("🚀 TaskFlow Manager Pro");
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        String[] cols = {"Priority","Task","Status"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);

        JPanel top = new JPanel();
        taskField = new JTextField(20);
        searchField = new JTextField(12);
        priorityBox = new JComboBox<>(new String[]{"High","Medium","Low"});

        JButton addBtn = new JButton("Add");
        JButton completeBtn = new JButton("Complete");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear All");
        JButton exportBtn = new JButton("Export");
        JButton searchBtn = new JButton("Search");

        top.add(new JLabel("Task"));
        top.add(taskField);
        top.add(priorityBox);
        top.add(addBtn);
        top.add(new JLabel("Search"));
        top.add(searchField);
        top.add(searchBtn);

        totalLabel = new JLabel("Total: 0");
        completedLabel = new JLabel("Completed: 0");
        pendingLabel = new JLabel("Pending: 0");
        statusLabel = new JLabel("Status: Ready");

        JPanel stats = new JPanel(new GridLayout(4,1));
        stats.add(totalLabel);
        stats.add(completedLabel);
        stats.add(pendingLabel);
        stats.add(statusLabel);

        JPanel actions = new JPanel();
        actions.add(completeBtn);
        actions.add(deleteBtn);
        actions.add(clearBtn);
        actions.add(exportBtn);

        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(actions, BorderLayout.SOUTH);
        frame.add(stats, BorderLayout.EAST);

        loadTasks();
        updateStats();

        addBtn.addActionListener(e -> {
            String task = taskField.getText().trim();
            if(!task.isEmpty()){
                model.addRow(new Object[]{priorityBox.getSelectedItem(), task, "Pending"});
                taskField.setText("");
                saveTasks();
                updateStats();
            }
        });

        completeBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r!=-1){
                model.setValueAt("Completed", r, 2);
                saveTasks();
                updateStats();
            }
        });

        deleteBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r!=-1){
                model.removeRow(r);
                saveTasks();
                updateStats();
            }
        });

        clearBtn.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(frame,"Clear all tasks?")==0){
                model.setRowCount(0);
                saveTasks();
                updateStats();
            }
        });

        exportBtn.addActionListener(e -> exportReport());

        searchBtn.addActionListener(e -> {
            String key = searchField.getText().toLowerCase();
            for(int i=0;i<model.getRowCount();i++){
                String t = model.getValueAt(i,1).toString().toLowerCase();
                if(t.contains(key)){
                    table.setRowSelectionInterval(i,i);
                    break;
                }
            }
        });

        frame.setVisible(true);
    }

    void updateStats(){
        int total=model.getRowCount(), comp=0;
        for(int i=0;i<total;i++){
            if("Completed".equals(model.getValueAt(i,2))) comp++;
        }
        totalLabel.setText("Total: "+total);
        completedLabel.setText("Completed: "+comp);
        pendingLabel.setText("Pending: "+(total-comp));
    }

    void saveTasks(){
        try(PrintWriter pw=new PrintWriter("tasks.txt")){
            for(int i=0;i<model.getRowCount();i++){
                pw.println(model.getValueAt(i,0)+"|"+model.getValueAt(i,1)+"|"+model.getValueAt(i,2));
            }
        }catch(Exception ignored){}
    }

    void loadTasks(){
        try(BufferedReader br=new BufferedReader(new FileReader("tasks.txt"))){
            String line;
            while((line=br.readLine())!=null){
                String[] p=line.split("\\|");
                if(p.length==3) model.addRow(p);
            }
        }catch(Exception ignored){}
    }

    void exportReport(){
        try(PrintWriter pw=new PrintWriter("report.txt")){
            for(int i=0;i<model.getRowCount();i++){
                pw.println(model.getValueAt(i,0)+" | "+model.getValueAt(i,1)+" | "+model.getValueAt(i,2));
            }
            statusLabel.setText("Status: Report Exported");
        }catch(Exception ex){
            statusLabel.setText("Status: Export Failed");
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TodoGUI::new);
    }
}
