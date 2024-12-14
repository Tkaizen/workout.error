import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WorkoutSchedulerApp {
    private JFrame frame;
    private JPanel mainPanel, taskListPanel, addTaskPanel;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JLabel timerLabel;

    public WorkoutSchedulerApp() {
        frame = new JFrame("Workout Scheduler");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        createMainView();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void createMainView() {
        // Task List Panel
        taskListPanel = new JPanel(new BorderLayout());
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        // Task list header
        JLabel taskListLabel = new JLabel("Workout Tasks", SwingConstants.CENTER);
        taskListPanel.add(taskListLabel, BorderLayout.NORTH);
        taskListPanel.add(taskScrollPane, BorderLayout.CENTER);

        // Buttons to add, edit, finish tasks
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addTaskButton = new JButton("Add Task");
        JButton editTaskButton = new JButton("Edit Task");
        JButton finishTaskButton = new JButton("Finish Task");

        addTaskButton.addActionListener(e -> switchToAddTaskView());
        editTaskButton.addActionListener(e -> editSelectedTask());
        finishTaskButton.addActionListener(e -> finishSelectedTask());

        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(editTaskButton);
        buttonsPanel.add(finishTaskButton);

        taskListPanel.add(buttonsPanel, BorderLayout.SOUTH);
        mainPanel.add(taskListPanel, BorderLayout.CENTER);
    }

    private void switchToAddTaskView() {
        // Create Add Task Panel
        addTaskPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        addTaskPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Task name input
        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField();
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();
        JLabel timerLabel = new JLabel("Timer (minutes):");
        JSpinner timerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 120, 1));

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> addTask(nameField, descField, timerSpinner));
        cancelButton.addActionListener(e -> switchToMainView());

        addTaskPanel.add(nameLabel);
        addTaskPanel.add(nameField);
        addTaskPanel.add(descLabel);
        addTaskPanel.add(descField);
        addTaskPanel.add(timerLabel);
        addTaskPanel.add(timerSpinner);
        addTaskPanel.add(confirmButton);
        addTaskPanel.add(cancelButton);

        frame.getContentPane().remove(mainPanel);
        frame.add(addTaskPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void addTask(JTextField nameField, JTextField descField, JSpinner timerSpinner) {
        String taskName = nameField.getText().trim();
        String taskDesc = descField.getText().trim();
        int timer = (int) timerSpinner.getValue();

        if (!taskName.isEmpty()) {
            String taskDetails = taskName + " - " + taskDesc + " | Timer: " + timer + " min";
            taskListModel.addElement(taskDetails);
            switchToMainView();
        } else {
            JOptionPane.showMessageDialog(frame, "Task name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedTask() {
        String selectedTask = taskList.getSelectedValue();
        if (selectedTask != null) {
            JTextField nameField = new JTextField(selectedTask.split(" - ")[0]);
            JTextField descField = new JTextField(selectedTask.split(" - ")[1].split("\\|")[0].trim());
            JSpinner timerSpinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(selectedTask.split("Timer: ")[1].split(" ")[0]), 1, 120, 1));

            Object[] editFields = {"Task Name:", nameField, "Description:", descField, "Timer (minutes):", timerSpinner};
            int result = JOptionPane.showConfirmDialog(frame, editFields, "Edit Task", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newTask = nameField.getText() + " - " + descField.getText() + " | Timer: " + timerSpinner.getValue() + " min";
                int index = taskList.getSelectedIndex();
                taskListModel.set(index, newTask);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a task to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finishSelectedTask() {
        String selectedTask = taskList.getSelectedValue();
        if (selectedTask != null) {
            taskListModel.removeElement(selectedTask);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a task to finish.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void switchToMainView() {
        frame.getContentPane().removeAll();
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void startWorkoutTimer(int minutes) {
        // Create a timer label if not already created
        if (timerLabel == null) {
            timerLabel = new JLabel("Time Remaining: " + minutes + ":00", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Serif", Font.BOLD, 24));
            frame.add(timerLabel, BorderLayout.SOUTH);
            frame.revalidate();
            frame.repaint();
        }

        // Convert minutes to seconds for countdown
        int totalSeconds = minutes * 60;

        // Timer that updates every second
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (totalSeconds > 0) {
                    totalSeconds--;
                    int minutesLeft = totalSeconds / 60;
                    int secondsLeft = totalSeconds % 60;
                    String timeText = String.format("Time Remaining: %d:%02d", minutesLeft, secondsLeft);
                    timerLabel.setText(timeText);
                } else {
                    ((Timer) e.getSource()).stop();
                    timerLabel.setText("Workout Finished!");
                }
            }
        });

        countdownTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkoutSchedulerApp());
    }
}
