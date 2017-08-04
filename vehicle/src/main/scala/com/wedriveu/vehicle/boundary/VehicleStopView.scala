package com.wedriveu.vehicle.boundary

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import com.wedriveu.vehicle.control.VehicleControl

/**
  * @author Michele Donati on 04/08/2017.
  */

/** This trait models the UI of vehicle stop window.*/
trait VehicleStopView {
  /** This method renders the frame visible.*/
  def render(): Unit

  /** This method does the shutdown of the system.*/
  def close(): Unit

  /** This method write the vehicles message logs in the text area.
    *
    * @param messageToLog This is the message to write.
    */
  def writeMessageLog(messageToLog: String): Unit

  /** This method associates the UI with the proper vehicle control system.
    *
    * @param vehicle This is the reference of the vehicle control system.
    */
  def setVehicleAssociated(vehicle: VehicleControl): Unit

}

class VehicleStopViewImpl(vehicleIdentifier: Int)
  extends JFrame("Vehicle " + vehicleIdentifier) with VehicleStopView with ActionListener {

  val notCommandFoundError: String = "No Command Found"
  val stateBroken: String = "broken"

  setDefaultCloseOperation(0)
  var vehicleAssociated: VehicleControl = null
  val panel: JPanel = new JPanel()
  val logLabel: JLabel = new JLabel("Log")
  val logsTextArea: JTextArea = new JTextArea(450,300)
  logsTextArea.setEditable(false)
  val textAreaScrollPane: JScrollPane = new JScrollPane(logsTextArea)
  val stopCommand: String = "Force Stop Vehicle (Broken)"
  val stopButton: JButton =  new JButton(stopCommand)
  setSize(500,400)
  setResizable(false)
  stopButton.addActionListener(this)

  val groupLayout: GroupLayout = new GroupLayout(panel)
  panel.setLayout(groupLayout)
  groupLayout.setAutoCreateGaps(true)
  groupLayout.setAutoCreateContainerGaps(true)

  groupLayout.setHorizontalGroup(
    groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(logLabel)
          .addComponent(textAreaScrollPane)
          .addComponent(stopButton)
    )
  )

  groupLayout.setVerticalGroup(
    groupLayout.createSequentialGroup()
        .addComponent(logLabel)
        .addComponent(textAreaScrollPane)
        .addComponent(stopButton)
  )

  add(panel)

  override def render(): Unit = setVisible(true)

  override def close(): Unit = System.exit(-1)

  override def writeMessageLog(messageToLog: String): Unit = {
    logsTextArea.setText(logsTextArea.getText.concat(messageToLog + "\n"))
  }

  override def setVehicleAssociated(vehicle: VehicleControl) : Unit = {
    vehicleAssociated = vehicle
  }

  override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
    case command if command == stopCommand =>
      vehicleAssociated.getVehicle().state = stateBroken
      //TODO Avviso il sistema che sono broken, nel senso che non viene più visto dal sistema (System.exit(0))

    case _ => println(notCommandFoundError)
  }

}
