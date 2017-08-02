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
    val yesCommand: String = "yes"
    val checkBoxYes: JCheckBox = new JCheckBox(yesCommand)
    val noCommand: String = "no"
    val checkBoxNo: JCheckBox = new JCheckBox(noCommand)
    val startCommand: String = "Start Vehicle"
    val startButton: JButton =  new JButton(startCommand)
    setSize(200,250)
    setResizable(false)
    startButton.addActionListener(this)
    checkBoxYes.addActionListener(this)
    checkBoxNo.addActionListener(this)

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
                   .addComponent(checkBoxYes)
                   .addComponent(checkBoxNo)
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
              .addComponent(checkBoxYes)
              .addComponent(checkBoxNo))
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
      else if(!checkBoxYes.isSelected && !checkBoxNo.isSelected){
        JOptionPane.showMessageDialog(this,
          "At least one option of 'Can Break?' checkbox should be selected.",
          "Break events not selected error",
          JOptionPane.ERROR_MESSAGE)
      }
      else {
        new VehicleCreator(batteryTextField.getText.toDouble, checkBoxYes.isSelected, checkBoxNo.isSelected)
      }
      case command if command == yesCommand => if(checkBoxNo.isSelected) {
        checkBoxNo.setSelected(false)
      }
      case command if command == noCommand => if(checkBoxYes.isSelected) {
        checkBoxYes.setSelected(false)
      }
      case _ => println("No Command Found")
    }

}
