<template>
  <container>
    <loading-content class="agents_wr" :status="agentsStatus" @reload="reloadAgents">
      <div class="agents">
        <div class="agent_wr"
             v-for="agent in agents"
             :key="agent.id"
             :class="{ expanded: agent.expanded }">
          <div class="agent">
            <div class="head" @click="agent.expanded = !agent.expanded">
              <div class="agent_arrow_sign"></div>
              <div class="agent_name">{{agent.name}}</div>
              <div class="agent_icons">
                <div class="head_icon" v-if="agent.updatePeriodSeconds > 0">
                  <img src="assets/icons/update_period.svg" alt="icon">
                </div>
              </div>
              <div class="agent_status">
                {{friendlyStatus(agent.status)}}
              </div>
            </div>
            <transition-expand>
              <div class="body_wr" v-if="agent.expanded">
                <div class="body">
                  <div class="top_row">
                    <div class="info id_info">
                      <span class="info_name">ID: </span>
                      <span class="info_value">{{agent.id}}</span>
                    </div>
                    <div class="info type_info">
                      <span class="info_name">Тип: </span>
                      <span class="info_value">{{getUserFriendlyAgentTypeName(agent.type)}}</span>
                    </div>
                    <div class="info last_run_info" v-if="agent.lastUpdateTimestamp">
                      <span class="info_name">Последний запуск: </span>
                      <span class="info_value">{{getUserFriendlyDate(agent.lastUpdateTimestamp)}}</span>
                    </div>
                    <div class="info last_run_info" v-else>
                      <span class="info_name">Еще не запускался</span>
                    </div>
                  </div>
                  <div class="info update_period_info" v-if="agent.updatePeriodSeconds > 0">
                    <div class="info_icon">
                      <img src="assets/icons/update_period.svg" alt="icon">
                    </div>
                    <span class="info_name">Обновляется каждые </span>
                    <span class="info_value">{{agent.updatePeriodSeconds}}</span>
                    <span class="info_name"> секунд</span>
                    <btn class="info_disable_update_btn" thin grey tiny @click="disableUpdate(agent)">Выключить</btn>
                  </div>
                  <transition-expand>
                    <div class="info incomplete_model_info" v-if="agent.lastAppliedDataEntryId !== agent.lastDataEntryId">
                      <div class="info_icon">
                        <img src="assets/icons/info.svg" alt="icon">
                      </div>
                      <span class="info_name">Ожидает обновления модели (</span>
                      <span class="info_value">{{(agent.lastDataEntryId === null ? -1 : agent.lastDataEntryId) - (agent.lastAppliedDataEntryId === null ? -1 : agent.lastAppliedDataEntryId)}} </span>
                      <span class="info_name">блоков из </span>
                      <span class="info_value">{{(agent.lastDataEntryId || 0) + 1}}</span>
                      <span class="info_name">)</span>
                    </div>
                  </transition-expand>
                  <transition-expand>
                    <div class="info error_info" v-if="agent.status === 'ERROR'">
                      <div class="info_icon">
                        <img src="assets/icons/info.svg" alt="icon">
                      </div>
                      <span class="info_value">{{ agent.memory['error_message'] || "Агент не смог выполниться, но сервер не сообщил почему" }}</span>
                    </div>
                  </transition-expand>
                </div>
              </div>
            </transition-expand>
          </div>
          <transition-expand>
            <div class="agent_controls_wr" v-if="agent.expanded">
              <div class="agent_controls">
                <btn small thin grey @click="openEditingDialog(agent)">&#9999; Редактировать</btn>
                <btn small thin grey @click="deleteDialogTargetId = agent.id">&#10060; Удалить</btn>
                <btn small thin grey v-if="agent.status !== 'RUNNING'" @click="runAgent(agent.id)">&#128640; Запустить сейчас</btn>
              </div>
            </div>
          </transition-expand>
        </div>
      </div>
      <div class="no_agents" v-if="agents.length === 0 && agentsStatus === 'ready'">Пусто...</div>
    </loading-content>

    <btn small grey thick class="add_btn" @click="createDialog = true">&#10133; Добавить</btn>

    <dialog-popup :show="createDialog" @close="createDialog = false">
      <div class="title">Новый агент</div>
      <text-input class="input"
                  hint="Название"
                  :error-hint="newAgentForm.name.error"
                  v-model="newAgentForm.name.value"/>
      <single-select class="input"
                     placeholder="Тип"
                     v-model="newAgentForm.type.value"
                     :error-hint="newAgentForm.type.error"
                     :options="agentTypes.map(type => { return { value: type.type, title: type.title }})"/>
      <transition-expand>
        <div class="agent_parameters" v-if="newAgentForm.type.value">
          <div class="agent_parameter"
               v-for="parameter in agentTypes.find(t => t.type === newAgentForm.type.value).parameters"
               :key="parameter.name">
            <text-input class="input"
                        v-model="newAgentForm.parameters[parameter.name].value"
                        :type="parameter.type"
                        :hint="parameter.title"
                        :error-hint="newAgentForm.parameters[parameter.name].error"/>
          </div>
        </div>
      </transition-expand>
      <toggle-switch class="input" v-model="newAgentForm.autoUpdateSwitch.value">Автообновление</toggle-switch>
      <transition-expand>
        <div v-if="newAgentForm.autoUpdateSwitch.value">
          <text-input class="input"
                      type="number"
                      hint="Период обновления (сек)"
                      :error-hint="newAgentForm.updatePeriodSeconds.error"
                      v-model="newAgentForm.updatePeriodSeconds.value"/>
        </div>
      </transition-expand>
      <toggle-switch class="input" v-model="newAgentForm.visible.value">Видимый</toggle-switch>
      <toggle-switch class="input" v-model="newAgentForm.sensitive.value">Чувствительный контент</toggle-switch>
      <loading-content :status="newAgentFormStatus" @reload="createAgent">
        <btn black thick medium class="dialog_button create_button" @click="createAgent">Создать</btn>
      </loading-content>
    </dialog-popup>

    <dialog-popup :show="editAgentFormTargetId != null" @close="editAgentFormTargetId = null">
      <div class="title">Редактирование</div>
      <text-input class="input"
                  hint="Название"
                  :error-hint="editAgentForm.name.error"
                  v-model="editAgentForm.name.value"/>
      <text-input class="input"
                  disabled
                  hint="Тип"
                  v-model="(agentTypes.find(t => t.type === editAgentForm.type.value) || {}).title"/>
      <transition-expand>
        <div class="agent_parameters" v-if="editAgentForm.type.value">
          <div class="agent_parameter"
               v-for="parameter in agentTypes.find(t => t.type === editAgentForm.type.value).parameters"
               :key="parameter.name">
            <text-input class="input"
                        v-model="editAgentForm.parameters[parameter.name].value"
                        :type="parameter.type"
                        :hint="parameter.title"
                        :error-hint="editAgentForm.parameters[parameter.name].error"/>
          </div>
        </div>
      </transition-expand>
      <toggle-switch class="input" v-model="editAgentForm.autoUpdateSwitch.value">Автообновление</toggle-switch>
      <transition-expand>
        <div v-if="editAgentForm.autoUpdateSwitch.value">
          <text-input class="input"
                      type="number"
                      hint="Период обновления (сек)"
                      :error-hint="editAgentForm.updatePeriodSeconds.error"
                      v-model="editAgentForm.updatePeriodSeconds.value"/>
        </div>
      </transition-expand>
      <toggle-switch class="input" v-model="editAgentForm.visible.value">Видимый</toggle-switch>
      <toggle-switch class="input" v-model="editAgentForm.sensitive.value">Чувствительный контент</toggle-switch>
      <loading-content :status="editAgentFormStatus" @reload="submitEditingDialog">
        <btn black thick medium class="dialog_button create_button" @click="submitEditingDialog">Сохранить</btn>
      </loading-content>
    </dialog-popup>

    <dialog-popup :show="deleteDialogTargetId" @close="deleteDialogTargetId = null">
      <div class="title">Точно удалить?</div>
      <loading-content :status="deleteDialogStatus" @reload="deleteAgent">
        <btn black thick medium class="dialog_button" @click="deleteAgent">Точно</btn>
        <btn black thick medium class="dialog_button" @click="deleteDialogTargetId = null">Не точно</btn>
      </loading-content>
    </dialog-popup>
  </container>
</template>

<script>
import Btn from "#/components/Btn";
import Container from "#/components/Container";
import DialogPopup from "#/components/dialogs/DialogPopup";
import LoadingContent from "#/components/LoadingContent";
import axios from "axios";
import TextInput from "#/components/TextInput";
import ToggleSwitch from "#/components/ToggleSwitch";
import TransitionExpand from "#/components/TransitionExpand";
import Vue from "vue";
import MessagePanel from "#/components/MessagePanel";
import SingleSelect from "#/components/SingleSelect";

export default {
  name: "Agents",
  components: {SingleSelect, MessagePanel, TransitionExpand, ToggleSwitch, TextInput, LoadingContent, DialogPopup, Container, Btn },
  data() {
    return {
      agents: [],
      agentTypes: [],
      agentsStatus: 'loading',
      createDialog: false,
      deleteDialogStatus: 'ready',
      deleteDialogTargetId: null,
      newAgentFormStatus: 'ready',
      newAgentForm: {
        name: { value: '', error: null },
        type: { value: '', error: null },
        updatePeriodSeconds: { value: '', error: null },
        autoUpdateSwitch: { value: false },
        sensitive: { value: false },
        visible: { value: true },
        parameters: {}
      },
      updaterIntervalId: null,
      editAgentFormTargetId: null,
      editAgentFormStatus: 'ready',
      editAgentForm: {
        name: { value: '', error: null },
        type: { value: '' },
        updatePeriodSeconds: { value: '', error: null },
        autoUpdateSwitch: { value: false },
        sensitive: { value: false },
        visible: { value: true },
        parameters: {}
      }
    }
  },
  mounted() {
    this.reloadAgents();
    this.startUpdater();
  },
  methods: {
    async reloadAgents() {
      let expandedMap = {};
      this.agents.forEach(agent => expandedMap[agent.id] = agent.expanded)
      try {
        this.agentsStatus = "loading";
        this.agents = (await axios.get('/api/manage/agent')).data.sort((a, b) => a.id - b.id);
        this.agents.forEach(agent => Vue.set(agent, 'expanded', expandedMap[agent.id] || false));
        this.agentTypes = (await axios.get('/api/manage/agent/type')).data;
        this.agentsStatus = "ready";
      } catch (e) {
        this.agentsStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    },

    startUpdater() {
      this.updaterIntervalId = setTimeout(this.backgroundUpdate, 2000)
    },

    async backgroundUpdate() {
      try {
        if (this.agentsStatus === 'ready') {
          let newAgents = (await axios.get('/api/manage/agent')).data.reduce((map, obj) => {
            map[obj.id] = obj;
            return map;
          }, {});
          for (let i = 0; i < this.agents.length; i++) {
            if (this.agents[i].id in newAgents) {
              let newAgent = newAgents[this.agents[i].id];
              Vue.set(this.agents, i, {...this.agents[i], ...newAgent});
            } else {
              this.agents.splice(i, 1);
              i--;
            }
          }
        }
      } catch (e) {
        console.error(e);
      } finally {
        this.updaterIntervalId = setTimeout(this.backgroundUpdate, 2000)
      }
    },

    friendlyStatus(status) {
      switch (status.toUpperCase()) {
        case "UNINITIALIZED": return "Новый";
        case "RUNNING": return "Работает";
        case "READY": return "Готов";
        case "ERROR": return "Есть проблемы";
      }
    },

    validateNotBlank(formEl) {
      if (formEl.value == null || formEl.value.trim().length === 0) {
        formEl.error = 'Это поле не должно быть пустым';
        return false;
      }
      formEl.error = null;
      return true;
    },

    validateNotNegative(formEl) {
      if (+formEl.value < 0) {
        formEl.error = 'Число не должно быть отрицательным';
        return false;
      }
      formEl.error = null;
      return true;
    },

    resetNewAgentForm() {
      let _ = this.newAgentForm;
      _.name.value = '';
      _.type.value = '';
      _.updatePeriodSeconds.value = '';
      _.visible.value = true;
      _.sensitive.value = false;
      _.parameters = {};
    },

    getUserFriendlyAgentTypeName(type) {
      return this.agentTypes.find(t => t.type === type).title
    },

    getUserFriendlyDate(timestamp) {
      let date = new Date(timestamp*1000);
      return `${date.getHours()}:${date.getMinutes()}:${date.getSeconds()} ${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    },

    openEditingDialog(agent) {
      this.editAgentFormTargetId = agent.id;
      this.editAgentFormStatus = 'ready';
      let _ = this.editAgentForm;
      _.name = { value: agent.name, error: null };
      _.type = { value: agent.type };
      _.updatePeriodSeconds = { value: Math.abs(agent.updatePeriodSeconds), error: null };
      _.autoUpdateSwitch = { value: agent.updatePeriodSeconds > 0 }
      _.sensitive = { value: agent.sensitive }
      _.visible = { value: agent.visible }
      _.parameters = {};
      if (_.type.value) {
        this.agentTypes.find(t => t.type === _.type.value).parameters.forEach(p =>
          _.parameters[p.name] = { value: agent.parameters[p.name], error: null }
        )
      }
    },

    async submitEditingDialog() {
      if (this.editAgentFormTargetId != null) {
        let form = this.editAgentForm;
        let ready = true;
        ready &= this.validateNotBlank(form.name);
        ready &= this.validateNotBlank(form.type);
        ready &= this.validateNotNegative(form.updatePeriodSeconds);
        if (form.type.value) {
          for (let parameter of this.agentTypes.find(t => t.type === form.type.value).parameters) {
            if (parameter.required) {
              ready &= this.validateNotBlank(form.parameters[parameter.name]);
            }
          }
        }
        if (ready) {
          this.editAgentFormStatus = 'loading';
          try {
            let parameters = {};
            for (let name in form.parameters)
              parameters[name] = form.parameters[name].value;

            await axios.post('/api/manage/agent', {
              id: this.editAgentFormTargetId,
              name: form.name.value,
              type: form.type.value,
              updatePeriodSeconds: form.autoUpdateSwitch.value ? +form.updatePeriodSeconds.value : -form.updatePeriodSeconds.value,
              sensitive: form.sensitive.value,
              visible: form.visible.value,
              parameters
            });
            this.editAgentFormStatus = 'ready';
            this.editAgentFormTargetId = null;
            await this.reloadAgents();
          } catch (e) {
            this.editAgentFormStatus = e.request.status === 0 ? 'offline' : 'error';
          }
        }
      }
    },

    async createAgent() {
      let ready = true;
      ready &= this.validateNotBlank(this.newAgentForm.name);
      ready &= this.validateNotBlank(this.newAgentForm.type);
      ready &= this.validateNotNegative(this.newAgentForm.updatePeriodSeconds);
      if (this.newAgentForm.type.value) {
        for (let parameter of this.agentTypes.find(t => t.type === this.newAgentForm.type.value).parameters) {
          if (parameter.required) {
            ready &= this.validateNotBlank(this.newAgentForm.parameters[parameter.name]);
          }
        }
      }
      if (ready) {
        this.newAgentFormStatus = 'loading';
        try {
          let parameters = {};
          for (let name in this.newAgentForm.parameters)
            parameters[name] = this.newAgentForm.parameters[name].value;

          let form = this.newAgentForm;
          await axios.put('/api/manage/agent', {
            name: form.name.value,
            type: form.type.value,
            updatePeriodSeconds: form.autoUpdateSwitch.value ? +form.updatePeriodSeconds.value : 0,
            sensitive: form.sensitive.value,
            visible: form.visible.value,
            parameters
          });
          this.newAgentFormStatus = 'ready';
          this.createDialog = false;
          this.resetNewAgentForm();
          await this.reloadAgents();
        } catch (e) {
          this.newAgentFormStatus = e.request.status === 0 ? 'offline' : 'error';
        }
      }
    },

    async runAgent(agentId) {
      try {
        this.agentsStatus = 'loading';
        await axios.post(`/api/manage/agent/${agentId}/run`);
        this.agentsStatus = 'ready';
        await this.reloadAgents();
      } catch (e) {
        if (e.request)
          this.agentsStatus = e.request.status === 0 ? 'offline' : 'error';
        else {
          this.agentsStatus = 'error';
          console.error(e);
        }
      }
    },

    async disableUpdate(agent) {
      try {
        this.agentsStatus = 'loading';
        await axios.post(`/api/manage/agent`, {
          id: agent.id,
          updatePeriodSeconds: -Math.abs(agent.updatePeriodSeconds)
        });
        this.agentsStatus = 'ready';
        await this.reloadAgents();
      } catch (e) {
        this.agentsStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    },

    async deleteAgent() {
      try {
        this.deleteDialogStatus = 'loading';
        await axios.delete(`/api/manage/agent/${this.deleteDialogTargetId}`);
        this.deleteDialogTargetId = null;
        this.deleteDialogStatus = 'ready';
        await this.reloadAgents();
      } catch (e) {
        this.deleteDialogStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    }
  },
  watch: {
    deleteDialogTargetId() {
      this.deleteDialogStatus = 'ready';
    },
    "newAgentForm.type.value": function() {
      this.newAgentForm.parameters = {};
      if (this.newAgentForm.type.value) {
        this.agentTypes.find(t => t.type === this.newAgentForm.type.value).parameters.forEach(p =>
          this.newAgentForm.parameters[p.name] = { value: '', error: null }
        )
      }
    }
  }
}
</script>

<style scoped lang="scss">
  .title {
    font-size: 24px;
    margin-bottom: 24px;
  }

  .agents_wr {
    margin-bottom: 25px;
  }

  .add_btn {
    width: 100%;
  }

  .no_agents {
    color: var(--grey);
  }

  .input {
    width: 100%;
    margin-bottom: 20px;
  }

  .dialog_button {
    min-width: 320px;
    margin-bottom: 20px;
  }

  .create_button {
    margin-top: 10px;
  }

  .agent_wr {
    margin-bottom: 18px;

    &.expanded .agent .agent_arrow_sign:before {
      transform: rotate(135deg);
    }

    .agent_controls {
      display: flex;
      flex-wrap: wrap;
      margin-right: -10px;
      margin-bottom: -10px;
      margin-top: 12px;

      .button {
        flex: 1;
        white-space: nowrap;
        margin-right: 10px;
        margin-bottom: 10px;
      }
    }
  }

  .agent {
    border-radius: 5px;
    outline: 1px solid var(--grey);
    box-shadow: 0 2px 0 var(--grey);

    .head {
      display: flex;
      align-items: center;
      padding: 20px;

      .agent_name {
        flex: 0;
        white-space: nowrap;
        margin-right: 15px;
        text-align: left;
      }

      .agent_status {
        font-size: 18px;
        color: var(--dark-grey);
      }

      .agent_icons {
        flex: 1;
      }

      .head_icon {
        width: 30px;
        height: 30px;
        filter: var(--dark-grey--svg-filter);

        & img {
          width: 100%;
          height: 100%;
        }
      }
    }

    .body {
      text-align: left;
      font-size: 18px;
      padding: 15px;
      overflow: auto;
      border-top: 1px solid var(--grey);
    }

    .info_icon {
      display: inline-block;
      height: 30px;
      width: 30px;
      position: absolute;
      left: 10px;
      top: 0;
      bottom: 0;
      margin: auto;
      filter: var(--dark-grey--svg-filter);

      & img {
        width: 100%;
        height: 100%;
      }
    }

    .top_row {
      display: flex;
      justify-content: space-around;
      flex-wrap: wrap;
    }

    .info {
      margin: 5px;
      text-align: center;

      .info_name {
        color: var(--grey)
      }
    }

    .update_period_info {
      background: var(--light-grey);
      border-radius: 5px;
      margin-top: 15px;
      position: relative;
      padding: 15px 15px 15px 50px;
      text-align: left;

      .info_name {
        color: var(--dark-grey);
      }
    }

    .info_disable_update_btn {
      margin-left: 10px;
    }

    .incomplete_model_info {
      background: var(--light-grey);
      border-radius: 5px;
      margin-top: 15px;
      display: flex;
      flex-wrap: wrap;
      position: relative;
      white-space: pre-wrap;
      padding: 15px 15px 15px 50px;
      text-align: left;

      .info_name {
        color: var(--dark-grey);
      }
    }

    .error_info {
      background: var(--just-a-little-red);
      border-radius: 5px;
      margin-top: 15px;
      display: flex;
      flex-wrap: wrap;
      position: relative;
      white-space: pre-wrap;
      padding: 15px 15px 15px 50px;
      text-align: left;
    }

    .agent_arrow_sign {
      width: 25px;
      position: relative;

      &:before {
        width: 12px;
        height: 12px;
        display: block;
        content: '';
        position: absolute;
        margin: auto;
        top: 0;
        bottom: 0;
        left: -8px;
        border-top: 3px solid var(--grey);
        border-right: 3px solid var(--grey);
        transform: rotate(45deg);
        transition: var(--default-transition), transform 200ms ease-in-out;
      }
    }
  }
</style>
