package med.voll.api.domain.consulta;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import med.voll.api.controller.DadosCancelamentoConsulta;
import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validations.ValidadorAgendamentoDeConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;

@Service
@RequiredArgsConstructor
public class AgendaDeConsultas {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final List<ValidadorAgendamentoDeConsulta> validadores;

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var paciente = pacienteRepository.findById(dadosAgendamentoConsulta.idPaciente())
                .orElseThrow(() -> new ValidacaoException("Paciente não encontrado"));
        if (!medicoRepository.existsById(dadosAgendamentoConsulta.idMedico())) {
            throw new ValidacaoException("Médico não encontrado");
        }

        validadores.forEach(validador -> validador.validate(dadosAgendamentoConsulta));

        var randomDoctor = chooseRandomDoctor(dadosAgendamentoConsulta);
        if (randomDoctor == null) {
            throw new ValidacaoException("Não foi possível encontrar um médico disponível");
        }

        var consulta = new Consulta(null, randomDoctor, paciente, dadosAgendamentoConsulta.data(), null);
        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);
    }

    private Medico chooseRandomDoctor(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        if (dadosAgendamentoConsulta.idMedico() != null) {
            return medicoRepository.findById(dadosAgendamentoConsulta.idMedico())
                    .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        }

        if (dadosAgendamentoConsulta.especialidade() == null) {
            throw new RuntimeException("Especialidade não informada");
        }

        return medicoRepository.escolherMedicoAleatorioLivreNaData(dadosAgendamentoConsulta.especialidade(),
                dadosAgendamentoConsulta.data());
    }

    public void cancelar(@Valid DadosCancelamentoConsulta dados) {
        var consulta = consultaRepository.findById(dados.idConsulta())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.cancelar(dados.motivo());
    }

}