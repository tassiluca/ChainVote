<script setup lang="ts">

import {library} from "@fortawesome/fontawesome-svg-core";
import {faCirclePlus, faCircleXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

defineProps({
  title: {
    type: String,
    required: true,
  },
  bigger: {
    type: String,
    required: false,
  },
  medium: {
    type: String,
    required: false,
  },
  smaller: {
    type: String,
    required: false,
  },
})

library.add(faCirclePlus, faCircleXmark);
</script>

<template>
  <div class="d-flex flex-column justify-content-center tile">
    <span id="title">{{title}}</span>
    <span v-if="bigger" id="bigger">{{bigger}}</span>
    <span v-if="medium" id="medium">{{medium}}</span>
    <span><slot /></span>
    <span v-if="smaller" id="smaller">{{smaller}}</span>
    <button v-if="$slots.details" type="button" class="btn" data-bs-toggle="modal" data-bs-target="#exampleModal">
      <font-awesome-icon class="text-primary" icon="circle-plus" />
    </button>
    <div v-if="$slots.details" class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-fullscreen">
        <div class="modal-content">
          <div class="row">
            <button type="button" class="btn" data-bs-dismiss="modal" aria-label="Close">
              <font-awesome-icon icon="circle-xmark" size="2x" />
            </button>
          </div>
          <div class="modal-body">
            <slot name="details" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
div:not(.modal) {
  border-radius: 15px;
  box-shadow: 1px 3px 15px rgba(200, 200, 200, 0.82);
  padding: 10px;
  margin: 10px 0;
  color: #0d6efd;

  span#title {
    font-weight: bold;
    font-size: 1.1em;
    color: #e6308a;
    text-transform: uppercase;
  }

  span#bigger {
    font-weight: bold;
    font-size: 1.5em;
    text-transform: uppercase;
  }

  span#medium {
    font-weight: bold;
    font-size: 1.2em;
    text-transform: uppercase;
  }

  span#smaller {
    font-size: 0.8em;
  }
}

div.modal div {
  margin: auto;
  box-shadow: none;
}

div.modal-body {
  width: 65%;
}

@media screen and (max-width: 992px) {
  div.modal-body {
    width: 100%;
  }
}
</style>