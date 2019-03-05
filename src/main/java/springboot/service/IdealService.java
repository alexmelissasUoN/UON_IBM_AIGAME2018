package springboot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springboot.domain.Ideal;
import springboot.domain.IdealRepository;
import springboot.domain.Player;
import springboot.util.AnalysisResult;

@Service("idealService")
public class IdealService {
	@Autowired
	private IdealRepository idealRepository;
	@Autowired
	private PlayerService playerService;

	public void addIdeal(Ideal ideal) {
		idealRepository.save(ideal);
	}

	public Iterable<Ideal> getIdeals() {
		return idealRepository.findAll();
	}

	public Ideal getIdealById(String id) {
		Ideal ideal = null;
		Optional<Ideal> refIdeal = idealRepository.findById(id);
		if(refIdeal.isPresent()) {
			ideal = refIdeal.get();
		}
		return ideal;
	}

	public void updateIdeal(String id, Ideal newIdeal) {
		// This method will only be called once when the user submit the ideal
		// personality

		idealRepository.findById(id).map(ideal -> {
			ideal.setAgreeableness(newIdeal.getAgreeableness());
			ideal.setConscientiousness(newIdeal.getConscientiousness());
			ideal.setEmotionalrange(newIdeal.getEmotionalrange());
			ideal.setExtraversion(newIdeal.getExtraversion());
			ideal.setOpeness(newIdeal.getOpeness());

			this.updatePlayer(id, ideal);
			return idealRepository.save(ideal);
		});
	}

	public void deleteIdealById(String id) {
		idealRepository.deleteById(id);
	}

	public void updatePlayer(String id, Ideal ideal) {
		AnalysisResult analysisResult = new AnalysisResult();
		String jsonResult = ideal.getJsonResult();

		System.out.println("------" + analysisResult);

		Player player = playerService.getPlayerById(id);
		if (player == null) {
			// Player do not exist
		} else if (jsonResult != null) {
			analysisResult.setJsonObject(jsonResult);
			analysisResult.generateFactor(ideal, player);
		} else {
			analysisResult.generateNormalFactor(player);
		}

		playerService.updatePlayer(id, player);
	}
}