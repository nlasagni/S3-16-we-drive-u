package com.wedriveu.vehicle.boundary

import java.awt.event.{ActionEvent, ActionListener, WindowAdapter, WindowEvent}
import javax.swing._

import com.wedriveu.vehicle.entity.VehicleCreator

/**
  * Created by Michele on 02/08/2017.
  */
trait VehicleConfiguratorView {

  def render(): Unit

  def close(): Unit

}

 class VehicleConfiguratorViewImpl()
    extends JFrame("Vehicle Simulator") with VehicleConfiguratorView with ActionListener {

    setLocationRelativeTo(null)
    val panel: JPanel = new JPanel()
    val parametersLabel: JLabel = new JLabel("Configuration Parameters")
    val batteryLabel: JLabel = new JLabel("Battery (%):")
    val batteryTextField: JTextField = new JTextField()
    batteryTextField.setEditable(true)
    val speedLabel: JLabel = new JLabel("Average speed (Km/h):")
    val speedTextField: JTextField = new JTextField("50")
    speedTextField.setEditable(false)
    val breakLabel: JLabel = new JLabel("Can Break?")
    val yesCommandBreak: String = "yes"
    val checkBoxYesBreak: JCheckBox = new JCheckBox(yesCommandBreak)
    val noCommandBreak: String = "no"
    val checkBoxNoBreak: JCheckBox = new JCheckBox(noCommandBreak)
    val stolenLabel: JLabel = new JLabel("Can be stolen?")
    val yesCommandStolen: String = "yes."
    val checkBoxYesStolen: JCheckBox = new JCheckBox(yesCommandStolen)
    val noCommandStolen: String = "no."
    val checkBoxNoStolen: JCheckBox = new JCheckBox(noCommandStolen)
    val startCommand: String = "Start Vehicle"
    val startButton: JButton =  new JButton(startCommand)
    setSize(200,250)
    setResizable(false)
    startButton.addActionListener(this)
    checkBoxYesBreak.addActionListener(this)
    checkBoxNoBreak.addActionListener(this)
    checkBoxYesStolen.addActionListener(this)
    checkBoxNoStolen.addActionListener(this)

    val groupLayout: GroupLayout = new GroupLayout(panel)
    panel.setLayout(groupLayout)
    groupLayout.setAutoCreateGaps(true)
    groupLayout.setAutoCreateContainerGaps(true)

    groupLayout.setHorizontalGroup(
       groupLayout.createSequentialGroup()
         .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addComponent(parametersLabel)
           .addGroup(groupLayout.createSequentialGroup()
                 .addComponent(batteryLabel)
                 .addComponent(batteryTextField))
             .addGroup(groupLayout.createSequentialGroup()
                  .addComponent(speedLabel)
                  .addComponent(speedTextField))
              .addComponent(breakLabel)
              .addGroup(groupLayout.createSequentialGroup()
                   .addComponent(checkBoxYesBreak)
                   .addComponent(checkBoxNoBreak)
              )
              .addComponent(stolenLabel)
              .addGroup(groupLayout.createSequentialGroup()
                .addComponent(checkBoxYesStolen)
                .addComponent(checkBoxNoStolen)
              )
              .addComponent(startButton)
      )
    )

    groupLayout.setVerticalGroup(
      groupLayout.createSequentialGroup()
        .addComponent(parametersLabel)
        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(batteryLabel)
              .addComponent(batteryTextField))
        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(speedLabel)
              .addComponent(speedTextField))
        .addComponent(breakLabel)
        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(checkBoxYesBreak)
              .addComponent(checkBoxNoBreak))
        .addComponent(stolenLabel)
        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(checkBoxYesStolen)
          .addComponent(checkBoxNoStolen))
        .addComponent(startButton)
    )

    add(panel)

    addWindowListener(new WindowAdapter {
      override def windowClosing(e: WindowEvent): Unit = close()
    })

    override def render(): Unit = setVisible(true)

    override def close(): Unit = System.exit(-1)

    override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
      case command if command == startCommand => if(batteryTextField.getText.isEmpty
        || ((batteryTextField.getText.toDouble) < 0.0)
        || ((batteryTextField.getText.toDouble) > 100.0) ){
        JOptionPane.showMessageDialog(this,
          "Battery value should be setted between 0 and 100.",
          "Battery not correctly setted error",
          JOptionPane.ERROR_MESSAGE)
      }
      else if(!checkBoxYesBreak.isSelected && !checkBoxNoBreak.isSelected){
        JOptionPane.showMessageDialog(this,
          "At least one option of 'Can Break?' checkbox should be selected.",
          "Break events not selected error",
          JOptionPane.ERROR_MESSAGE)
      }
      else if(!checkBoxYesStolen.isSelected && !checkBoxNoStolen.isSelected){
        JOptionPane.showMessageDialog(this,
          "At least one option of 'Can be Stolen?' checkbox should be selected.",
          "Stolen events not selected error",
          JOptionPane.ERROR_MESSAGE)
      }
      else {
        new VehicleCreator(batteryTextField.getText.toDouble, checkBoxYesBreak.isSelected, checkBoxNoBreak.isSelected)
      }
      case command if command == yesCommandBreak => if(checkBoxNoBreak.isSelected) {
        checkBoxNoBreak.setSelected(false)
      }
      case command if command == noCommandBreak => if(checkBoxYesBreak.isSelected) {
        checkBoxYesBreak.setSelected(false)
      }
      case command if command == yesCommandStolen => if(checkBoxNoStolen.isSelected) {
        checkBoxNoStolen.setSelected(false)
      }
      case command if command == noCommandStolen => if(checkBoxYesStolen.isSelected) {
        checkBoxYesStolen.setSelected(false)
      }
      case _ => println("No Command Found")
    }

}