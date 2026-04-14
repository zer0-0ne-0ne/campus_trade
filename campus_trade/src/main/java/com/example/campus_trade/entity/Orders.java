package com.example.campus_trade.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Orders {
  private long oid;
  private long pid;
  private long buyerId;
  private long sellerId;
  private String tradeTime;
  private String tradePlace;
  private long status;
  private Timestamp createTime;
}