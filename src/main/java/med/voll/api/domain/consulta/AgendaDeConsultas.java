package med.voll.api.domain.consulta;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import med.voll.api.controller.DadosCancelamentoConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;

@Service
@RequiredArgsConstructor
public class AgendaDeConsultas {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public void agendar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var paciente = pacienteRepository.findById(dadosAgendamentoConsulta.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        var medico = medicoRepository.findById(dadosAgendamentoConsulta.idMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        var medico1 = chooseRandomDoctor(dadosAgendamentoConsulta);

        var consulta = new Consulta(null, medico, paciente, dadosAgendamentoConsulta.data(), null);
        consultaRepository.save(consulta);
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