package com.wedriveu.services.vehicle.boundary.nearest.entity;

import com.wedriveu.services.vehicle.rabbitmq.UserRequest;

/**
 * @author Marco Baldassarri
 * @since 02/08/2017
 */
public interface UserDataFactory {

    UserRequest getUserData();

}
