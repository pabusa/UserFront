package com.userfront.service;

import java.util.List;

import com.userfront.domain.Appointment;

public interface AppointmentService {
	public Appointment createAppointment(Appointment appointment);
	public List<Appointment> findAll();
	public Appointment findAppointment(Long id);
	public void confirmAppointment(Long id);
}
