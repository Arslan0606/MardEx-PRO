package uz.star.mardexworker.ui.screen.entry_activity.intro_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.model.response.intro.IntroData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.title.Title
import javax.inject.Inject

class IntroRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val localStorage: LocalStorage
) : IntroRepository, SafeApiRequest() {

    override fun getIntroData(): LiveData<ResultData<List<IntroData>>> {
        val resultLiveData = MutableLiveData<ResultData<List<IntroData>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getIntro(localStorage.language) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(if (it.data.isNotEmpty()) it.data else getIntroFromLocal())
                    else
                        resultLiveData.value = ResultData.data(getIntroFromLocal())
                }
                data?.onMessage {
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.data(getIntroFromLocal())
                    }
                }
            }
        )
        return resultLiveData
    }

    private fun getIntroFromLocal(): List<IntroData> {
        return listOf(
            IntroData(
                "",
                "",
                Title(
                    "Ish topish biz bilan juda oson !",
                    "С нами найти работу очень просто!",
                    "Иш топиш биз билан жуда осон !"
                ),
                Title(
                    "Tezkor topish",
                    "Быстрый поиск",
                    "Тезкор топиш"
                ),
                R.drawable.ic_intro1
            ),
            IntroData(
                "",
                "",
                Title(
                    "Bilgan kasb turlaringizni tanlang !",
                    "Выбирайте знакомые вам профессии!",
                    "Билган касб турларингизни танланг !"
                ),
                Title(
                    "Barcha kasblar mavjud",
                    "Доступны все профессии",
                    "Барча касблар мавжуд"
                ),
                R.drawable.ic_intro2
            ),
            IntroData(
                "",
                "",
                Title(
                    "Tez va oson ish toping va turli bonuslarga ega boʼling.",
                    "Быстро и легко находите работу и получайте различные бонусы.",
                    "Тез ва осон иш топинг ва турли бонусларга эга бўлинг."
                ),
                Title(
                    "Izlashingiz mumkin",
                    "Вы можете начать поиск",
                    "Излашингиз мумкин"
                ),
                R.drawable.ic_intro3
            )
        )
    }

}