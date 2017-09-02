package com.wedriveu.vehicle.boundary

import java.awt.event.{ActionEvent, ActionListener, WindowAdapter, WindowEvent}
import javax.swing._

import com.wedriveu.shared.util.Constants
import com.wedriveu.vehicle.control.VehicleControl
import com.wedriveu.vehicle.shared.VehicleConstants
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject

/**
  * @author Michele Donati on 04/08/2017.
  */

/** This trait models the UI of vehicle stop window. */
trait VehicleStopView {
  /** This method renders the frame visible. */
  def render(): Unit

  /** This method does the shutdown of the system. */
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

class VehicleStopViewImpl(vertx: Vertx, vehicleIdentifier: Int)
    extends JFrame("Vehicle " + vehicleIdentifier) with VehicleStopView with ActionListener {

  val eventBus: EventBus = vertx.eventBus()
  val notCommandFoundError: String = "No Command Found"
  val forceBrokenStatus: String = "Vehicle forced to be broken. Vehicle state is: "

  var vehicleAssociated: VehicleControl = null
  val panel: JPanel = new JPanel()
  val logLabel: JLabel = new JLabel("Log")
  val logsTextArea: JTextArea = new JTextArea(450, 300)
  logsTextArea.setEditable(false)
  val textAreaScrollPane: JScrollPane = new JScrollPane(logsTextArea)
  val stopCommand: String = "Force Stop Vehicle (Broken)"
  val stopButton: JButton = new JButton(stopCommand)
  setSize(500, 400)
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

  addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      forceStop()
      unsubscribeToEvents()
      dispose()
    }
  })

  override def render(): Unit = setVisible(true)

  override def close(): Unit = {
    eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleAssociated.getVehicle().plate),
      new JsonObject())
    writeMessageLog(forceBrokenStatus + vehicleAssociated.getVehicle().getState())
  }

  override def writeMessageLog(messageToLog: String): Unit = {
    logsTextArea.setText(logsTextArea.getText.concat(messageToLog + "\n"))
  }

  override def setVehicleAssociated(vehicle: VehicleControl): Unit = {
    vehicleAssociated = vehicle
  }

  override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
    case command if command == stopCommand => forceStop()
    case _ => println(notCommandFoundError)
  }

  private def forceStop(): Unit = {
    vehicleAssociated.getVehicle().setState(Constants.Vehicle.STATUS_BROKEN_STOLEN)
    eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleAssociated.getVehicle().plate),
      new JsonObject())
    writeMessageLog(forceBrokenStatus + vehicleAssociated.getVehicle().getState())
  }

  private def unsubscribeToEvents(): Unit = {
    vehicleAssociated.unsubscribeToBrokenEvents()
    vehicleAssociated.unsubscribeToStolenEvents()
  }

}
