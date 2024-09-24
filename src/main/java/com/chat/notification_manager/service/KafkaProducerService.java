package com.chat.notification_manager.service;

import com.chat.notification_manager.docunent.Notification;

public interface KafkaProducerService {
  void sendNewRegistryEvent(Notification notification);
}
