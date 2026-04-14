package com.example.campus_trade.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class User {
  private long uid;
  private String username;
  private String password;
  private String phone;
  private String email;
  private String identity;
  private String avatar;
  private Timestamp registerTime;
  private long status;
}