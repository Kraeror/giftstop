<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-3xl font-bold text-center text-gray-800 mb-6">Редактирай продукт</h1>

    <form @submit.prevent="submitProduct" class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 space-y-6">
      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="title">Заглавие</label>
        <input v-model="product.title" id="title" type="text" placeholder="Заглавие"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="description">Описание</label>
        <quill-editor
          v-model:value="product.description"
          contentType="html"
          theme="snow"
          class="bg-white rounded shadow border"
          style="min-height: 200px"
        />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="slug">Slug (линк)</label>
        <input v-model="product.slug" id="slug" type="text" placeholder="my-product"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2">Избери категория</label>
        <CategoryPicker
          :categories="allCategories"
          :selected-categories="product.categoryIds"
          @select="onCategorySelected"
          @unselect="onCategoryRemoved"
        />
      </div>

      <div class="mb-2">
        <label class="block text-gray-700 text-sm font-bold mb-2">Снимки</label>
        <input ref="imageInput" accept="image/*" type="file" multiple @change="handleImageUpload" class="hidden" />

        <button @click.prevent="$refs.imageInput.click()" type="button"
          class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring">
          📷 Качи снимки
        </button>

        <div class="flex gap-2 mt-2 flex-wrap justify-center">
          <div v-for="(img, index) in product.images" :key="index" class="relative">
            <img :src="img.preview || img" class="w-[100px] h-[100px] object-cover rounded border" />
            <button @click.prevent="removeImage(index)"
              class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
          </div>
        </div>
      </div>

      <div class="space-y-6">
        <h2 class="text-lg font-semibold text-gray-800">Варианти</h2>
        <div v-for="(variant, index) in product.variants" :key="index" class="space-y-4 border p-4 rounded shadow-md bg-gray-50">
          <div v-if="!variant.defaultVariant">
            <label class="block text-sm font-medium text-gray-700 mb-1">Име на вариант</label>
            <input v-model="variant.title" placeholder="Име на вариант"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Цена (лв)</label>
            <input v-model.number="variant.price" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Промо цена (лв)</label>
            <input v-model.number="variant.salePrice" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              <input type="radio" name="defaultVariant" :value="index" :checked="variant.defaultVariant" @change="setDefaultVariant(index)" />
              Основен вариант
            </label>
          </div>

          <div v-if="!variant.defaultVariant">
            <label class="block text-sm font-medium text-gray-700 mb-1">Снимка на варианта</label>
            <input type="file" accept="image/*" :ref="'variantImageInput_' + index" @change="handleVariantImageUpload($event, index)" class="hidden" />
            <button @click.prevent="openVariantImageInput(index)" type="button"
              class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600">
              📷 Качи снимка
            </button>
            <div v-if="variant.imagePreview" class="mt-2 relative w-[100px] h-[100px] mx-auto">
              <img :src="variant.imagePreview" class="w-full h-full object-cover rounded border" />
              <button @click.prevent="removeVariantImage(index)" class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
            </div>
          </div>

          <div class="space-y-2">
            <h3 class="text-sm font-semibold text-gray-700">Допълнителни полета</h3>
            <div v-for="(field, fieldIndex) in variant.customFields" :key="fieldIndex" class="border p-4 rounded shadow space-y-2">
              <div class="flex gap-4 items-center">
                <input v-model="field.name" placeholder="Име на полето" class="flex-1 border rounded px-3 py-1" />
                <select v-model="field.type" @change="onFieldTypeChange(field)" class="border rounded px-2 py-1">
                  <option disabled value="">Избери тип</option>
                  <option v-for="type in additionalFieldTypes" :key="type" :value="type">{{ type }}</option>
                </select>
                <button @click.prevent="variant.customFields.splice(fieldIndex, 1)" class="text-red-500">✕</button>
              </div>

              <div v-if="field.type === 'Текст (свободен)'" class="text-gray-500 italic">Клиентът ще въведе свободен текст.</div>
              <div v-else-if="field.type === 'Име'" class="text-gray-500 italic">Клиентът трябва да въведе само букви.</div>
              <div v-else-if="field.type === 'Числово поле'" class="text-gray-500 italic">Клиентът ще въведе само числа.</div>
              <div v-else-if="field.type === 'Дата'" class="text-gray-500 italic">Клиентът ще избере дата.</div>
              <div v-else-if="field.type === 'Снимка'" class="text-gray-500 italic">Клиентът ще прикачи снимка.</div>
              <div v-else-if="field.type === 'Цвят'" class="space-y-2">
                <div class="flex flex-wrap gap-2">
                  <div v-for="(color, i) in field.items" :key="i" class="flex flex-col items-center border p-2 rounded">
                    <input type="color" v-model="color.value" class="w-[50px] h-[30px] rounded" />
                    <input v-model="color.name" placeholder="Име на цвят" class="mt-1 text-xs border px-2 py-1 rounded w-[90px]" />
                    <button @click.prevent="field.items.splice(i, 1)" class="text-red-500 text-xs mt-1">✕</button>
                  </div>
                </div>
                <button @click.prevent="field.items.push({ name: '', value: '#000000' })" class="text-sm text-blue-600 hover:underline mt-2">+ Добави цвят</button>
              </div>
              <div v-else-if="field.type === 'Падащо меню'" class="space-y-2">
                <div v-for="(item, i) in field.items" :key="i" class="flex gap-2 items-center">
                  <input v-model="field.items[i]" placeholder="Опция" class="flex-1 border px-3 py-1 rounded" />
                  <button @click.prevent="field.items.splice(i, 1)" class="text-red-500">✕</button>
                </div>
                <button @click.prevent="field.items.push('')" class="text-sm text-blue-600 hover:underline">+ Нова опция</button>
              </div>
            </div>
            <button @click.prevent="variant.customFields.push({ name: '', type: '', items: [] })" class="text-sm text-blue-600 hover:underline">+ Добави поле</button>
          </div>

          <button v-if="product.variants.length > 1" @click.prevent="removeVariant(index)" class="text-red-500 hover:underline text-sm">Премахни варианта</button>
        </div>
        <button @click.prevent="addVariant" class="text-[#F777AC] hover:underline text-sm">+ Добави вариант</button>
      </div>

      <div class="pt-6">
        <button type="submit" class="bg-[#F777AC] hover:bg-pink-600 text-white font-bold py-2 px-4 rounded w-full">Запази промените</button>
      </div>
    </form>
  </div>
</template>


<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { quillEditor } from 'vue3-quill';
import 'quill/dist/quill.snow.css';
import CategoryPicker from '@/components/CategoryPicker.vue';

const route = useRoute();
const router = useRouter();

const additionalFieldTypes = [
  'Текст (свободен)',
  'Числово поле',
  'Име',
  'Снимка',
  'Цвят',
  'Падащо меню',
  'Дата'
];

const product = ref({
  title: '',
  description: '',
  slug: '',
  categoryIds: [],
  images: [],
  variants: []
});

const allCategories = ref([]);

onMounted(async () => {
  try {
    const res = await axios.get(`/api/admin/products/${route.params.id}`);
    const p = res.data;
    product.value = {
      ...p,
      categoryIds: p.categoryIds,
      images: p.imageFileNames.map(name => ({ preview: `http://192.168.0.68:8080/uploads/products/${p.id}/${name}` })),
      variants: p.variants.map(v => ({
        ...v,
        imageFile: null,
        imagePreview: v.imageFileName ? `http://192.168.0.68:8080/uploads/products/${p.id}/${v.imageFileName}` : null,
        customFields: v.customFields || []
      }))
    };

    const catRes = await axios.get('/api/admin/categories');
    allCategories.value = catRes.data;
  } catch (e) {
    console.error('Грешка при зареждане:', e);
  }
});

const onCategorySelected = (categoryId) => {
  if (!product.value.categoryIds.includes(categoryId)) {
    product.value.categoryIds.push(categoryId);
  }
};

const onCategoryRemoved = (categoryId) => {
  product.value.categoryIds = product.value.categoryIds.filter(id => id !== categoryId);
};

const handleImageUpload = (event) => {
  const newFiles = Array.from(event.target.files);
  const newImageObjects = newFiles.map(file => ({ file, preview: URL.createObjectURL(file) }));
  product.value.images.push(...newImageObjects);
};

const removeImage = (index) => {
  product.value.images.splice(index, 1);
};

const addVariant = () => {
  product.value.variants.push({
    title: '',
    price: 0,
    salePrice: 0,
    active: true,
    defaultVariant: false,
    imageFile: null,
    imagePreview: null,
    customFields: []
  });
};

const removeVariant = (index) => {
  if (product.value.variants.length === 1) {
    alert('Не може да изтриете последния вариант.');
    return;
  }
  const wasDefault = product.value.variants[index].defaultVariant;
  product.value.variants.splice(index, 1);
  if (wasDefault && product.value.variants.length > 0) {
    product.value.variants[0].defaultVariant = true;
    product.value.variants[0].title = '';
  }
};

const handleVariantImageUpload = (event, index) => {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    product.value.variants[index].imageFile = file;
    product.value.variants[index].imagePreview = reader.result;
  };
  reader.readAsDataURL(file);
};

const removeVariantImage = (index) => {
  product.value.variants[index].imageFile = null;
  product.value.variants[index].imagePreview = null;
};

const openVariantImageInput = (index) => {
  const input = document.querySelector(`input[ref='variantImageInput_${index}']`);
  if (input) input.click();
};

const setDefaultVariant = (index) => {
  product.value.variants.forEach((v, i) => {
    v.defaultVariant = i === index;
    if (v.defaultVariant) {
      v.title = '';
    }
  });
};

const onFieldTypeChange = (field) => {
  field.items = [];
  field.preview = null;
  field.file = null;
  field.value = '';

  switch (field.type) {
    case 'Текст (свободен)': field.name = 'Текст'; break;
    case 'Числово поле': field.name = 'Число'; break;
    case 'Име': field.name = 'Име'; break;
    case 'Снимка': field.name = 'Прикачи снимка'; break;
    case 'Цвят':
      field.name = 'Цвят';
      field.items = [{ name: '', value: '#000000' }];
      break;
    case 'Падащо меню':
      field.name = 'Избери опция';
      field.items = [''];
      break;
    case 'Дата':
      field.name = 'Дата';
      break;
  }
};

watch(() => product.value.variants, (variants) => {
  const defaultIndex = variants.findIndex(v => v.defaultVariant);
  variants.forEach((v, i) => {
    if (v.defaultVariant && i !== defaultIndex) v.defaultVariant = false;
  });
}, { deep: true });

const submitProduct = async () => {
  try {
    const formData = new FormData();

    const request = {
      title: product.value.title,
      description: product.value.description,
      slug: product.value.slug,
      categoryIds: product.value.categoryIds,
      imageFileNames: [],
      variants: product.value.variants.map(v => ({
        title: v.title,
        price: v.price,
        salePrice: v.salePrice,
        active: v.active,
        imageFileName: '',
        defaultVariant: v.defaultVariant,
        customFields: v.customFields || []
      }))
    };

    formData.append('product', new Blob([JSON.stringify(request)], { type: 'application/json' }));

    for (const image of product.value.images) {
      if (image.file) {
        formData.append('images', image.file);
      }
    }

    product.value.variants.forEach((variant, i) => {
      const variantDto = request.variants[i];
      formData.append('variants', new Blob([JSON.stringify(variantDto)], { type: 'application/json' }));

      if (variant.imageFile) {
        formData.append('variantImages', variant.imageFile);
      }

      for (const field of variant.customFields || []) {
        if (field.type === 'Снимка' && field.file) {
          formData.append('customFieldImages', field.file);
        }
      }
    });

    await axios.put(`/api/admin/products/${product.value.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    alert('Продуктът е обновен успешно!');
    router.push('/admin/products');
  } catch (e) {
    console.error('Грешка при обновяване:', e);
    alert('Възникна грешка при запис.');
  }
};
</script>