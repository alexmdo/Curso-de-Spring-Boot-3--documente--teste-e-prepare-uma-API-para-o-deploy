package med.voll.api.domain.consulta.validations;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;

@Component
public class ValidadorHorarioAntecendencia implements ValidadorAgendamentoDeConsulta {

    @Override
    public void validate(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var appointmentDate = dadosAgendamentoConsulta.data();
        var now = LocalDateTime.now();
        var differenceInMinutes = Duration.between(now, appointmentDate).toMinutes();

        if (differenceInMinutes < 30) {
            throw new ValidacaoException("A consulta deve ser agendada com no mínimo 30 minutos de antecedência");
        }
    }

}
