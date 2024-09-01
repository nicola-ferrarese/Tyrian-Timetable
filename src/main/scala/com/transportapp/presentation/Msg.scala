package com.transportapp.presentation

import com.transportapp.application.commands.Command
import com.transportapp.domain.events.AppEvent

enum Msg:
  case ExecuteCommand(command: Command)
  case HandleEvent(event: AppEvent)
  case ToggleAppMode
  case NoOp