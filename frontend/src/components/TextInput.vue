<template>
  <label class="wrapper">
    <span class="input_visual" :data-state-wrong="errorHint != null">
      <span class="input_wr">
        <span class="input_prefix" v-if="prefix">
          {{ prefix }}
        </span>
        <input  class="input"
                :type="type ? type : 'text'"
                v-model="localText"
                @keydown.esc="$event.target.blur()">
        <span class="input_hint" :class="{ active: localText.length !== 0 }" v-if="hint">{{ hint }}</span>
      </span>
      <span class="ending_wr">
        <slot name="ending"/>
      </span>
    </span>

    <transition-expand>
      <div class="err_msg" v-if="errorHint != null">
        {{ errorHint }}
      </div>
    </transition-expand>
  </label>
</template>

<script>
import TransitionExpand from "#/components/TransitionExpand";
export default {
  components: { TransitionExpand },
  props: {
    hint: String,
    value: {
      type: String,
      default: ''
    },
    prefix: String,
    errorHint: String,
    type: String
  },
  data() {
    return {
      localText: this.value === undefined ? '' : this.value
    }
  },
  watch: {
    value() {
      this.localText = this.value === undefined ? '' : this.value;
    },
    localText() {
      this.$emit('input', this.localText);
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  margin-bottom: 10px;
  display: inline-block;
}
.input_wr {
  flex: 1;
  display: flex;
  padding: 10px;
  font-size: 22px;
  line-height: 22px;
  position: relative;
  justify-content: flex-start;
}
.input_visual {
  width: 100%;
  display: flex;
  border-radius: 4px;
  border: 1px var(--grey) solid;
  background: var(--background);
  cursor: text;
}
.input_prefix {
  opacity: .5;
  font-size: inherit;
}

.input_hint {
  color: var(--grey);
  position: absolute;
  top: 25%;
  padding: 0 5px;
  font-size: 0.8em;
  transition: var(--default-transition), top 200ms ease-in-out;
  background: var(--background);

  &.active {
    color: var(--dark-grey);
    top: -25%;
  }
}

.input {
  border: none;
  background: none;
  outline: none;
  font-size: inherit;
  width: 100%;
  padding: 0;
  flex: 1;

  &:focus + .input_hint {
    color: var(--dark-grey);
    top: -25%;
  }
}

[data-state-wrong='true'] {
  border: 1px solid rgba(255, 0, 0, .5);
  box-shadow: 0 0 2px red;
}

.err_msg {
  color: darkred;
  font-size: 16px;
}
</style>
