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
              <div class="agent_status">{{friendlyStatus(agent.status)}}</div>
            </div>
            <transition-expand>
              <div class="body_wr" v-if="agent.expanded">
                <div class="body">
                  <div>id {{agent.id}}</div>
                  <div>Тип: {{agent.type}}</div>
                  <div>Последний запуск: {{agent.lastUpdateTimestamp}}</div>
                  <div>Период обновления: {{agent.updatePeriodSeconds}}</div>
                </div>
              </div>
            </transition-expand>
          </div>
          <transition-expand>
            <div class="agent_controls_wr" v-if="agent.expanded">
              <div class="agent_controls">
<!--                TODO-->
                <btn small thin grey>&#9999; Редактировать</btn>
                <btn small thin grey @click="deleteDialogTargetId = agent.id">&#10060; Удалить</btn>
<!--                TODO-->
                <btn small thin grey v-if="agent.status !== 'RUNNING'">&#128640; Запустить сейчас</btn>
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
      <text-input class="input"
                  type="number"
                  hint="Период обновления (сек)"
                  :error-hint="newAgentForm.updatePeriodSeconds.error"
                  v-model="newAgentForm.updatePeriodSeconds.value"/>
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
      <toggle-switch class="input" v-model="newAgentForm.visible.value">Видимый</toggle-switch>
      <toggle-switch class="input" v-model="newAgentForm.sensitive.value">Чувствительный контент</toggle-switch>
      <loading-content :status="newAgentFormStatus" @reload="createAgent">
        <btn black thick medium class="dialog_button create_button" @click="createAgent">Создать</btn>
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
        visible: { value: true },
        sensitive: { value: false },
        parameters: {}
      }
    }
  },
  mounted() {
    this.reloadAgents();
  },
  methods: {
    async reloadAgents() {
      try {
        this.agentsStatus = "loading";
        this.agents = (await axios.get('/api/manage/agent')).data;
        this.agents.forEach(agent => Vue.set(agent, 'expanded', false));
        this.agentTypes = (await axios.get('/api/manage/agent/type')).data;
        this.agentsStatus = "ready";
      } catch (e) {
        this.agentsStatus = e.request.status === 0 ? 'offline' : 'error';
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
      this.newAgentForm.name.value = '';
      this.newAgentForm.type.value = '';
      this.newAgentForm.updatePeriodSeconds.value = '';
      this.newAgentForm.visible.value = true;
      this.newAgentForm.sensitive.value = false;
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

          await axios.put('/api/manage/agent', {
            name: this.newAgentForm.name.value,
            type: this.newAgentForm.type.value,
            updatePeriodSeconds: +this.newAgentForm.updatePeriodSeconds.value,
            sensitive: this.newAgentForm.sensitive.value,
            visible: this.newAgentForm.visible.value,
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
        flex: 1;
        text-align: left;
      }

      .agent_status {
        font-size: 18px;
        color: var(--dark-grey);
      }
    }

    .body {
      font-size: 18px;
      padding: 15px;
      overflow: auto;
      border-top: 1px solid var(--grey);
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
