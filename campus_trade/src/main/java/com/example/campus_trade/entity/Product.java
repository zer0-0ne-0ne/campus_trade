package com.example.campus_trade.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Product {
  private long pid;
  private String title;
  private String description;
  private double price;
  private String condition;
  private String keywords;
  private String imagePath;
  private long cid;
  private long uid;
  private long status;
  private Timestamp releaseTime;
}