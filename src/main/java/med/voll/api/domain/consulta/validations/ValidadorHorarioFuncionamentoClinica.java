package med.voll.api.domain.consulta.validations;

import org.springframework.stereotype.Component;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsulta {

    @Override
    public void validate(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var appointmentDate = dadosAgendamentoConsulta.data();

        var isSunday = appointmentDate.getDayOfWeek().getValue() == 7;
        var isBeforeOpeningTime = appointmentDate.getHour() < 7;
        var isAfterClosingTime = appointmentDate.getHour() > 18;

        if (isSunday || isBeforeOpeningTime || isAfterClosingTime) {
            throw new ValidacaoException("Horário de funcionamento da clínica: Segunda a Sábado, das 7h às 18h");
        }
    }

}
