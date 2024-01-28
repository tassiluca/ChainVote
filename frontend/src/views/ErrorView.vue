<template>
  <Breadcrumb :paths="[]" />
  <PageTitle title="Error" />
  <div class="text-center err">
    Oops, seems something went wrong, try again please.
  </div>
</template>

<script setup lang="ts">
import Breadcrumb from "@/components/BreadcrumbComponent.vue";
import PageTitle from "@/components/PageTitleComponent.vue";
</script>

<style scoped>
div.err {
  font-size: x-large;
}
</style>