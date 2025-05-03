// Adicionando selo de recomendado à primeira opção da lista
let firstTitleDiv = document.querySelector('.pf-l-stack__item.select-auth-box-headline.pf-c-title');

firstTitleDiv.style.display = 'flex';
firstTitleDiv.style.flexDirection = 'row';
firstTitleDiv.style.alignItems = 'center';
firstTitleDiv.style.justifyContent = 'space-between';

let recommendedBadge = document.createElement('span');
recommendedBadge.innerText = 'Recomendado';
recommendedBadge.style.fontSize = '12px';
recommendedBadge.style.fontWeight = 'normal';
recommendedBadge.style.backgroundColor = 'var(--pf-global--primary-color--200)';
recommendedBadge.style.borderRadius = '12px';
recommendedBadge.style.padding = '0px 12px';
recommendedBadge.style.color = '#FFF';

firstTitleDiv.appendChild(recommendedBadge);