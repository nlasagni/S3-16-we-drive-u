package com.wedriveu.vehicle.boundary

import java.awt.event.{ActionEvent, ActionListener, WindowAdapter, WindowEvent}
import javax.swing._

import com.wedriveu.vehicle.control.VehicleCreator
import com.wedriveu.vehicle.shared.VehicleConstants
import io.vertx.core.{AsyncResult, Vertx}

/**
  * @author Michele Donati 02/08/2017.
  */

/** This trait models the UI of the vehicles configurator file. */
trait VehicleConfiguratorView {
  /** This method renders the frame visible. */
  def render(): Unit

  /** This method does the shutdown of the system. */
  def close(): Unit

}

class VehicleConfiguratorViewImpl()
    extends JFrame("Vehicle Simulator") with VehicleConfiguratorView with ActionListener {

  val valueUnderZero: Double = 0.0
  val valueOverOneHundred: Double = 100.0
  val speedMinorBound: Double = 20.0
  val speedMaxBound: Double = 100.0
  val errorInSpeedValueMessage: String = "Speed value should be set between 20 and 100 Km/h"
  val errorInSpeedValueMessageTitle: String = "Speed not correctly set error"
  val errorInBatteryValueMessage: String =
    "Battery value should be set between " + VehicleConstants.RechargingThreshold + " and 100."
  val errorInBatteryValueMessageTitle: String = "Battery not correctly set error"
  val errorInCanBreakMessage: String = "At least one option of 'Can Break?' checkbox should be selected."
  val errorInCanBreakMessageTitle: String = "Break events not selected error"
  val errorInCanStolenMessage: String = "At least one option of 'Can be Stolen?' checkbox should be selected."
  val errorInCanStolenMessageTitle: String = "Stolen events not selected error"
  val errorInCanStolenAndBrokenMessage: String = "Not both Broken and Stolen options can be selected."
  val errorInCanStolenAndBrokenMessageTitle: String = "Both options selected error."
  val notCommandFoundError: String = "No Command Found"

  setLocationRelativeTo(null)
  var vehiclesCounter: Int = 1
  var indexForImages: Int = 0
  val panel: JPanel = new JPanel()
  val parametersLabel: JLabel = new JLabel("Configuration Parameters")
  val batteryLabel: JLabel = new JLabel("Battery (%):")
  val batteryTextField: JTextField = new JTextField()
  batteryTextField.setEditable(true)
  val speedLabel: JLabel = new JLabel("Average speed (Km/h):")
  val speedTextField: JTextField = new JTextField()
  speedTextField.setEditable(true)
  val breakLabel: JLabel = new JLabel("Can Break?")
  val yesCommandBreak: String = "yes"
  val checkBoxYesBreak: JCheckBox = new JCheckBox(yesCommandBreak)
  val noCommandBreak: String = "no"
  val checkBoxNoBreak: JCheckBox = new JCheckBox(noCommandBreak)
  val startCommand: String = "Start Vehicle"
  val startButton: JButton = new JButton(startCommand)
  setSize(200, 250)
  setResizable(false)
  startButton.addActionListener(this)
  checkBoxYesBreak.addActionListener(this)
  checkBoxNoBreak.addActionListener(this)

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
        .addComponent(startButton)
  )

  add(panel)

  addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = close()
  })

  override def render(): Unit = setVisible(true)

  override def close(): Unit = System.exit(-1)

  override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
    case command if command == startCommand => if (batteryTextField.getText.isEmpty
        || (batteryTextField.getText.toDouble < VehicleConstants.RechargingThreshold)
        || (batteryTextField.getText.toDouble > valueOverOneHundred)) {
      JOptionPane.showMessageDialog(this, errorInBatteryValueMessage,
        errorInBatteryValueMessageTitle,
        JOptionPane.ERROR_MESSAGE)
    }
    else if (speedTextField.getText.isEmpty
        || (speedTextField.getText.toDouble < speedMinorBound)
        || (speedTextField.getText.toDouble > speedMaxBound)) {
      JOptionPane.showMessageDialog(this, errorInSpeedValueMessage,
        errorInSpeedValueMessageTitle,
        JOptionPane.ERROR_MESSAGE)
    }
    else if (!checkBoxYesBreak.isSelected && !checkBoxNoBreak.isSelected) {
      JOptionPane.showMessageDialog(this,
        errorInCanBreakMessage,
        errorInCanBreakMessageTitle,
        JOptionPane.ERROR_MESSAGE)
    }
    else {
      if (indexForImages == 6) {
        indexForImages = 0
      }
      val vertx: Vertx = Vertx.vertx()
      vertx.executeBlocking((async: io.vertx.core.Future[Object]) => {
        new VehicleCreator(vertx,
          speedTextField.getText.toDouble,
          batteryTextField.getText.toDouble,
          checkBoxYesBreak.isSelected,
          vehiclesCounter,
          indexForImages).start()
        vehiclesCounter += 1
        indexForImages += 1
        async.complete()
      }, (result: AsyncResult[Object]) => {})
    }
    case command if command == yesCommandBreak => if (checkBoxNoBreak.isSelected) {
      checkBoxNoBreak.setSelected(false)
    }
    case command if command == noCommandBreak => if (checkBoxYesBreak.isSelected) {
      checkBoxYesBreak.setSelected(false)
    }
    case _ => println(notCommandFoundError)
  }

}
